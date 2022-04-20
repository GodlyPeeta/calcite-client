package org.kamiblue.client.setting

import org.kamiblue.client.CalciteMod
import org.kamiblue.client.setting.configs.NameableConfig

internal object GenericConfig : NameableConfig<GenericConfigClass>(
    "generic",
    "${CalciteMod.DIRECTORY}config/"
)