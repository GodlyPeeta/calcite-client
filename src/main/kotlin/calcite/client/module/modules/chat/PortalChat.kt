package calcite.client.module.modules.chat

import calcite.client.mixin.client.player.MixinEntityPlayerSP
import calcite.client.module.Category
import calcite.client.module.Module

/**
 * @see MixinEntityPlayerSP
 */
internal object PortalChat : Module(
    name = "PortalChat",
    category = Category.CHAT,
    description = "Allows you to open GUIs in portals",
    showOnArray = false
)
