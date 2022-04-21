package calcite.client.module.modules.client

import calcite.client.module.Category
import calcite.client.module.Module

internal object Tooltips : Module(
    name = "Tooltips",
    description = "Displays handy module descriptions in the GUI",
    category = Category.CLIENT,
    showOnArray = false,
    enabledByDefault = true
)
