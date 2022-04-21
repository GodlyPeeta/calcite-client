package calcite.client.setting

import calcite.client.CalciteMod
import calcite.client.module.AbstractModule
import calcite.client.module.modules.client.Configurations
import calcite.client.setting.configs.NameableConfig
import java.io.File

internal object ModuleConfig : NameableConfig<AbstractModule>(
    "modules",
    "${calcite.client.CalciteMod.DIRECTORY}config/modules",
) {
    override val file: File get() = File("$filePath/${Configurations.modulePreset}.json")
    override val backup get() = File("$filePath/${Configurations.modulePreset}.bak")
}