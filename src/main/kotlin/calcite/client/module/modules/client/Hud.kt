package calcite.client.module.modules.client

import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.color.ColorHolder

internal object Hud : Module(
    name = "Hud",
    description = "Toggles Hud displaying and settings",
    category = Category.CLIENT,
    showOnArray = false,
    enabledByDefault = true
) {
    val hudFrame by setting("Hud Frame", false)
    val primaryColor by setting("Primary Color", ColorHolder(255, 255, 255), false)
    val secondaryColor by setting("Secondary Color", ColorHolder(144, 255, 144), false)
}