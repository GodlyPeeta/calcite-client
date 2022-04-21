package calcite.client.gui.hudgui

import calcite.client.gui.rgui.Component
import calcite.client.setting.GuiConfig
import calcite.client.setting.settings.SettingRegister

internal abstract class LabelHud(
    name: String,
    alias: Array<String> = emptyArray(),
    category: Category,
    description: String,
    alwaysListening: Boolean = false,
    enabledByDefault: Boolean = false
) : AbstractLabelHud(name, alias, category, description, alwaysListening, enabledByDefault, GuiConfig),
    SettingRegister<Component> by GuiConfig