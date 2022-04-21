package calcite.client.module.modules.combat

import net.minecraft.client.gui.GuiGameOver
import calcite.client.event.events.GuiEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.event.listener.listener

internal object AntiDeathScreen : Module(
    name = "AntiDeathScreen",
    description = "Fixes random death screen glitches",
    category = Category.COMBAT
) {
    init {
        listener<GuiEvent.Displayed> {
            if (it.screen !is GuiGameOver) return@listener
            if (mc.player.health > 0) {
                mc.player.respawnPlayer()
                mc.displayGuiScreen(null)
            }
        }
    }
}