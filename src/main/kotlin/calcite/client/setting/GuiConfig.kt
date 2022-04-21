package calcite.client.setting

import calcite.client.CalciteMod
import calcite.client.gui.rgui.Component
import calcite.client.module.modules.client.Configurations
import calcite.client.plugin.api.IPluginClass
import calcite.client.setting.configs.AbstractConfig
import calcite.client.setting.configs.PluginConfig
import calcite.client.setting.settings.AbstractSetting
import java.io.File

internal object GuiConfig : AbstractConfig<Component>(
    "gui",
    "${calcite.client.CalciteMod.DIRECTORY}config/gui"
) {
    override val file: File get() = File("$filePath/${Configurations.guiPreset}.json")
    override val backup get() = File("$filePath/${Configurations.guiPreset}.bak")

    override fun addSettingToConfig(owner: Component, setting: AbstractSetting<*>) {
        if (owner is IPluginClass) {
            (owner.config as PluginConfig).addSettingToConfig(owner, setting)
        } else {
            val groupName = owner.settingGroup.groupName
            if (groupName.isNotEmpty()) {
                getGroupOrPut(groupName).getGroupOrPut(owner.name).addSetting(setting)
            }
        }
    }
}