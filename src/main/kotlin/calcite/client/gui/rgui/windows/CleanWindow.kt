package calcite.client.gui.rgui.windows

import calcite.client.gui.rgui.WindowComponent
import calcite.client.setting.GuiConfig
import calcite.client.setting.configs.AbstractConfig
import calcite.commons.interfaces.Nameable

/**
 * Window with no rendering
 */
open class CleanWindow(
    name: String,
    posX: Float,
    posY: Float,
    width: Float,
    height: Float,
    settingGroup: SettingGroup,
    config: AbstractConfig<out Nameable> = GuiConfig
) : WindowComponent(name, posX, posY, width, height, settingGroup, config)