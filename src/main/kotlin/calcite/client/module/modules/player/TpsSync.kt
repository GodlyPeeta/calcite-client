package calcite.client.module.modules.player

import calcite.client.module.Category
import calcite.client.module.Module

internal object TpsSync : Module(
    name = "TpsSync",
    description = "Synchronizes block states with the server TPS",
    category = Category.PLAYER
)
