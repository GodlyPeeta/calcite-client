package calcite.client.module.modules.combat

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumHand
import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.event.SafeClientEvent
import calcite.client.manager.managers.CombatManager
import calcite.client.manager.managers.HotbarManager
import calcite.client.manager.managers.PlayerPacketManager
import calcite.client.manager.managers.PlayerPacketManager.sendPlayerPacket
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.TickTimer
import calcite.client.util.TimeUnit
import calcite.client.util.TpsCalculator
import calcite.client.util.combat.CombatUtils
import calcite.client.util.combat.CombatUtils.equipBestWeapon
import calcite.client.util.combat.CombatUtils.scaledHealth
import calcite.client.util.items.isWeapon
import calcite.client.util.math.RotationUtils.faceEntityClosest
import calcite.client.util.math.RotationUtils.getRotationToEntityClosest
import calcite.client.util.threads.safeListener
import calcite.commons.interfaces.DisplayEnum

@CombatManager.CombatModule
internal object KillAura : Module(
    name = "KillAura",
    alias = arrayOf("KA", "Aura", "TriggerBot"),
    category = Category.COMBAT,
    description = "Hits entities around you",
    modulePriority = 50
) {
    private val mode by setting("Mode", Mode.COOLDOWN)
    private val rotationMode by setting("Rotation Mode", RotationMode.SPOOF)
    private val attackDelay by setting("Attack Delay", 5, 1..40, 1, { mode == Mode.TICKS })
    private val disableOnDeath by setting("Disable On Death", false)
    private val tpsSync by setting("TPS Sync", false)
    private val weaponOnly by setting("Weapon Only", false)
    private val autoWeapon by setting("Auto Weapon", true)
    private val prefer by setting("Prefer", CombatUtils.PreferWeapon.SWORD, { autoWeapon })
    private val minSwapHealth by setting("Min Swap Health", 5.0f, 1.0f..20.0f, 0.5f)
    private val swapDelay by setting("Swap Delay", 10, 0..50, 1)
    val range by setting("Range", 4.0f, 0.0f..6.0f, 0.1f)

    private val timer = TickTimer(TimeUnit.TICKS)
    private var inactiveTicks = 0

    private enum class Mode(override val displayName: String) : DisplayEnum {
        COOLDOWN("Cooldown"),
        TICKS("Ticks")
    }

    @Suppress("UNUSED")
    private enum class RotationMode(override val displayName: String) : DisplayEnum {
        OFF("Off"),
        SPOOF("Spoof"),
        VIEW_LOCK("View Lock")
    }

    override fun isActive(): Boolean {
        return isEnabled && inactiveTicks <= 5
    }

    override fun getHudInfo(): String {
        return mode.displayName
    }

    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (it.phase != TickEvent.Phase.START) return@safeListener

            inactiveTicks++

            if (!player.isEntityAlive) {
                if (disableOnDeath) disable()
                return@safeListener
            }
            val target = CombatManager.target ?: return@safeListener

            if (!CombatManager.isOnTopPriority(KillAura) || CombatSetting.pause) return@safeListener
            if (player.getDistance(target) >= range) return@safeListener
            if (player.scaledHealth > minSwapHealth && autoWeapon) equipBestWeapon(prefer)
            if (weaponOnly && !player.heldItemMainhand.item.isWeapon) return@safeListener
            if (swapDelay > 0 && System.currentTimeMillis() - HotbarManager.swapTime < swapDelay * 50L) return@safeListener

            inactiveTicks = 0
            rotate(target)
            if (canAttack()) attack(target)
        }
    }

    private fun SafeClientEvent.rotate(target: EntityLivingBase) {
        when (rotationMode) {
            RotationMode.SPOOF -> {
                sendPlayerPacket {
                    rotate(getRotationToEntityClosest(target))
                }
            }
            RotationMode.VIEW_LOCK -> {
                faceEntityClosest(target)
            }
            else -> {
                // Rotation off
            }
        }
    }

    private fun SafeClientEvent.canAttack() =
        when (mode) {
            Mode.COOLDOWN -> {
                val adjustTicks = if (!tpsSync) 0.0f
                else TpsCalculator.adjustTicks
                player.getCooledAttackStrength(adjustTicks) > 0.9f
            }
            Mode.TICKS -> {
                timer.tick(attackDelay)
            }
        }

    private fun SafeClientEvent.attack(entity: Entity) {
        playerController.attackEntity(player, entity)
        player.swingArm(EnumHand.MAIN_HAND)
    }
}