package calcite.client.module.modules.misc

import net.minecraft.client.gui.GuiDisconnected
import net.minecraft.client.multiplayer.GuiConnecting
import net.minecraft.client.multiplayer.ServerData
import calcite.client.event.events.GuiEvent
import calcite.client.mixin.extension.message
import calcite.client.mixin.extension.parentScreen
import calcite.client.mixin.extension.reason
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.StopTimer
import calcite.event.listener.listener
import kotlin.math.max

internal object AutoReconnect : Module(
    name = "AutoReconnect",
    description = "Automatically reconnects after being disconnected",
    category = Category.MISC,
    alwaysListening = true
) {
    private val delay = setting("Delay", 5.0f, 0.5f..100.0f, 0.5f)

    private var prevServerDate: ServerData? = null

    init {
        listener<GuiEvent.Closed> {
            if (it.screen is GuiConnecting) prevServerDate = mc.currentServerData
        }

        listener<GuiEvent.Displayed> {
            if (isDisabled || (prevServerDate == null && mc.currentServerData == null)) return@listener
            (it.screen as? GuiDisconnected)?.let { gui ->
                it.screen = KamiGuiDisconnected(gui)
            }
        }
    }

    private class KamiGuiDisconnected(disconnected: GuiDisconnected) : GuiDisconnected(disconnected.parentScreen, disconnected.reason, disconnected.message) {
        private val timer = StopTimer()

        override fun updateScreen() {
            if (timer.stop() >= (delay.value * 1000.0f)) {
                mc.displayGuiScreen(GuiConnecting(parentScreen, mc, mc.currentServerData ?: prevServerDate ?: return))
            }
        }

        override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
            super.drawScreen(mouseX, mouseY, partialTicks)
            val ms = max(delay.value * 1000.0f - timer.stop(), 0.0f).toInt()
            val text = "Reconnecting in ${ms}ms"
            fontRenderer.drawString(text, width / 2f - fontRenderer.getStringWidth(text) / 2f, height - 32f, 0xffffff, true)
        }
    }
}
