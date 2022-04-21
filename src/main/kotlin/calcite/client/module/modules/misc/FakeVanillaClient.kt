package calcite.client.module.modules.misc

import calcite.client.module.Category
import calcite.client.module.Module

internal object FakeVanillaClient : Module(
    name = "FakeVanillaClient",
    description = "Fakes a modless client when connecting",
    category = Category.MISC
)
