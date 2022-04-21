package calcite.client.gui.hudgui.window

import calcite.client.gui.hudgui.AbstractHudElement
import calcite.client.gui.rgui.windows.SettingWindow
import calcite.client.setting.settings.AbstractSetting

class HudSettingWindow(
    hudElement: AbstractHudElement,
    posX: Float,
    posY: Float
) : SettingWindow<AbstractHudElement>(hudElement.name, hudElement, posX, posY, SettingGroup.NONE) {

    override fun getSettingList(): List<AbstractSetting<*>> {
        return element.settingList
    }

}