package calcite.client.plugin.api

import calcite.client.gui.hudgui.AbstractHudElement
import calcite.client.setting.settings.SettingRegister

abstract class PluginHudElement(
    final override val pluginMain: Plugin,
    name: String,
    alias: Array<String> = emptyArray(),
    category: Category,
    description: String,
    alwaysListening: Boolean = false,
    enabledByDefault: Boolean = false
) : AbstractHudElement(name, alias, category, description, alwaysListening, enabledByDefault, pluginMain.config),
    IPluginClass,
    SettingRegister<IPluginClass> by pluginMain.config
