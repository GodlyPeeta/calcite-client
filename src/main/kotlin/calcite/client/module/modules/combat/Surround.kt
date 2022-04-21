package calcite.client.module.modules.combat

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.event.SafeClientEvent
import calcite.client.manager.managers.CombatManager
import calcite.client.manager.managers.HotbarManager.resetHotbar
import calcite.client.manager.managers.HotbarManager.spoofHotbar
import calcite.client.manager.managers.PlayerPacketManager.sendPlayerPacket
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.module.modules.movement.Strafe
import calcite.client.util.*
import calcite.client.util.EntityUtils.flooredPosition
import calcite.client.util.MovementUtils.centerPlayer
import calcite.client.util.MovementUtils.speed
import calcite.client.util.combat.SurroundUtils
import calcite.client.util.combat.SurroundUtils.checkHole
import calcite.client.util.items.HotbarSlot
import calcite.client.util.items.firstBlock
import calcite.client.util.items.hotbarSlots
import calcite.client.util.math.VectorUtils.toBlockPos
import calcite.client.util.text.MessageSendHelper
import calcite.client.util.threads.defaultScope
import calcite.client.util.threads.isActiveOrFalse
import calcite.client.util.threads.safeListener
import calcite.client.util.world.buildStructure
import calcite.client.util.world.isPlaceable

@CombatManager.CombatModule
internal object Surround : Module(
    name = "Surround",
    category = Category.COMBAT,
    description = "Surrounds you with obsidian to take less damage",
    modulePriority = 200
) {
    private val placeSpeed by setting("Places Per Tick", 4f, 0.25f..5f, 0.25f)
    private val disableStrafe by setting("Disable Strafe", true)
    private val strictDirection by setting("Strict Direction", false)
    private val autoDisable by setting("Auto Disable", AutoDisableMode.OUT_OF_HOLE)
    private val outOfHoleTimeout by setting("Out Of Hole Timeout", 10, 1..50, 5, { autoDisable == AutoDisableMode.OUT_OF_HOLE }, description = "Delay before disabling Surround when you are out of hole, in ticks")
    private val enableInHole = setting("Enable In Hole", false)
    private val inHoleTimeout by setting("In Hole Timeout", 50, 1..100, 5, { enableInHole.value }, description = "Delay before enabling Surround when you are in hole, in ticks")
    private val toggleMessage by setting("Toggle Message", true)

    private enum class AutoDisableMode {
        ONE_TIME, OUT_OF_HOLE
    }

    private val toggleTimer = StopTimer(TimeUnit.TICKS)

    private var holePos: BlockPos? = null
    private var job: Job? = null

    override fun isActive(): Boolean {
        return isEnabled && job.isActiveOrFalse
    }

    init {
        onDisable {
            resetHotbar()
            holePos = null
        }

        onToggle {
            toggleTimer.reset()
        }

        safeListener<TickEvent.ClientTickEvent> {
            if (getObby() == null) return@safeListener

            if (isDisabled) {
                enableInHoleCheck()
                return@safeListener
            }

            // Following codes will not run if disabled
            // Update hole pos
            if (holePos == null || inHoleCheck()) {
                holePos = player.positionVector.toBlockPos()
            }

            // Out of hole check
            if (player.positionVector.toBlockPos() != holePos) {
                outOfHoleCheck()
                return@safeListener
            } else {
                toggleTimer.reset()
            }

            // Placeable
            if (!canRun()) {
                if (autoDisable == AutoDisableMode.ONE_TIME) disable()
                return@safeListener
            }

            // Centered check
            if (!player.centerPlayer()) return@safeListener

            if (disableStrafe) {
                Strafe.disable()
            }

            // The actual job
            if (!job.isActiveOrFalse) {
                job = runSurround()
            } else if (job.isActiveOrFalse) {
                spoofObby()
                sendPlayerPacket {
                    cancelAll()
                }
            } else if (isEnabled && CombatManager.isOnTopPriority(Surround)) {
                resetHotbar()
            }
        }
    }

    private fun SafeClientEvent.enableInHoleCheck() {
        if (enableInHole.value && inHoleCheck()) {
            if (toggleTimer.stop() > inHoleTimeout) {
                if (toggleMessage) MessageSendHelper.sendChatMessage("$chatName You are in hole for longer than $inHoleTimeout ticks, enabling")
                enable()
            }
        } else {
            toggleTimer.reset()
        }
    }

    private fun SafeClientEvent.inHoleCheck() = player.onGround && player.speed < 0.15 && checkHole(player) == SurroundUtils.HoleType.OBBY

    private fun outOfHoleCheck() {
        if (autoDisable == AutoDisableMode.OUT_OF_HOLE) {
            if (toggleTimer.stop() > outOfHoleTimeout) {
                if (toggleMessage) MessageSendHelper.sendChatMessage("$chatName You are out of hole for longer than $outOfHoleTimeout ticks, disabling")
                disable()
            }
        }
    }

    private fun SafeClientEvent.spoofObby() {
        getObby()?.let { spoofHotbar(it) }
    }

    private fun SafeClientEvent.getObby(): HotbarSlot? {
        val slots = player.hotbarSlots.firstBlock(Blocks.OBSIDIAN)

        if (slots == null) { // Obsidian check
            if (isEnabled) {
                if (toggleMessage) MessageSendHelper.sendChatMessage("$chatName No obsidian in hotbar, disabling!")
                disable()
            }
            return null
        }

        return slots
    }

    private fun SafeClientEvent.canRun(): Boolean {
        val playerPos = player.positionVector.toBlockPos()
        return SurroundUtils.surroundOffset.any {
            world.isPlaceable(playerPos.add(it), true)
        }
    }

    private fun SafeClientEvent.runSurround() = defaultScope.launch {
        spoofObby()

        buildStructure(
            player.flooredPosition,
            SurroundUtils.surroundOffset,
            placeSpeed,
            2,
            4.25f,
            strictDirection
        ) {
            isEnabled && CombatManager.isOnTopPriority(Surround)
        }
    }

    init {
        alwaysListening = enableInHole.value
        enableInHole.listeners.add {
            alwaysListening = enableInHole.value
        }
    }
}
