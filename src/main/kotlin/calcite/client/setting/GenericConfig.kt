package calcite.client.setting

import calcite.client.CalciteMod
import calcite.client.setting.configs.NameableConfig

internal object GenericConfig : NameableConfig<GenericConfigClass>(
    "generic",
    "${calcite.client.CalciteMod.DIRECTORY}config/"
)