package calcite.client.module.modules.client

import calcite.client.event.events.ShutdownEvent
import calcite.client.gui.hudgui.CalciteHudGui
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.event.listener.listener

internal object HudEditor : Module(
    name = "HudEditor",
    description = "Edits the Hud",
    category = Category.CLIENT,
    showOnArray = false
) {
    init {
        onEnable {
            if (mc.currentScreen !is CalciteHudGui) {
                ClickGUI.disable()
                mc.displayGuiScreen(CalciteHudGui)
                CalciteHudGui.onDisplayed()
            }
        }

        onDisable {
            if (mc.currentScreen is CalciteHudGui) {
                mc.displayGuiScreen(null)
            }
        }

        listener<ShutdownEvent> {
            disable()
        }
    }
}
