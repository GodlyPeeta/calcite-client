package calcite.client.gui.mc

import net.minecraft.client.gui.GuiButton
import calcite.client.module.modules.player.ChestStealer
import calcite.client.util.Wrapper

class KamiGuiStoreButton(x: Int, y: Int) :
    GuiButton(420420, x, y, 50, 20, "Store") {
    override fun mouseReleased(mouseX: Int, mouseY: Int) {
        if (ChestStealer.mode.value === ChestStealer.Mode.MANUAL) {
            ChestStealer.storing = false
            playPressSound(Wrapper.minecraft.soundHandler)
        }
        super.mouseReleased(mouseX, mouseY)
    }
}