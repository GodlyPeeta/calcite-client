package calcite.client.module.modules.misc

import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.command.CommandManager
import calcite.client.event.SafeClientEvent
import calcite.client.event.events.BaritoneCommandEvent
import calcite.client.event.events.ConnectionEvent
import calcite.client.mixin.extension.sendClickBlockToController
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.BaritoneUtils
import calcite.client.util.text.MessageSendHelper
import calcite.client.util.text.formatValue
import calcite.client.util.threads.runSafe
import calcite.client.util.threads.runSafeR
import calcite.client.util.threads.safeListener
import calcite.event.listener.listener

internal object AutoMine : Module(
    name = "AutoMine",
    description = "Automatically mines chosen ores",
    category = Category.MISC
) {

    private val manual by setting("Manual", false)
    private val iron = setting("Iron", false)
    private val diamond = setting("Diamond", true)
    private val gold = setting("Gold", false)
    private val coal = setting("Coal", false)
    private val log = setting("Logs", false)

    init {
        onEnable {
            runSafeR {
                run()
            } ?: disable()
        }

        onDisable {
            BaritoneUtils.cancelEverything()
        }
    }

    private fun SafeClientEvent.run() {
        if (isDisabled || manual) return

        val blocks = ArrayList<String>()

        if (iron.value) blocks.add("iron_ore")
        if (diamond.value) blocks.add("diamond_ore")
        if (gold.value) blocks.add("gold_ore")
        if (coal.value) blocks.add("coal_ore")
        if (log.value) {
            blocks.add("log")
            blocks.add("log2")
        }

        if (blocks.isEmpty()) {
            MessageSendHelper.sendBaritoneMessage("Error: you have to choose at least one thing to mine. " +
                "To mine custom blocks run the ${formatValue("${CommandManager.prefix}b mine block")} command")
            BaritoneUtils.cancelEverything()
            return
        }

        MessageSendHelper.sendBaritoneCommand("mine", *blocks.toTypedArray())
    }

    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (manual) {
                mc.sendClickBlockToController(true)
            }
        }

        listener<ConnectionEvent.Disconnect> {
            disable()
        }

        listener<BaritoneCommandEvent> {
            if (it.command.contains("cancel")) {
                disable()
            }
        }

        with({ runSafe { run() } }) {
            iron.listeners.add(this)
            diamond.listeners.add(this)
            gold.listeners.add(this)
            coal.listeners.add(this)
            log.listeners.add(this)
        }
    }
}
