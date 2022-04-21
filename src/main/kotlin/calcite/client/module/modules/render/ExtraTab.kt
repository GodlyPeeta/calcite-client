package calcite.client.module.modules.render

import net.minecraft.client.network.NetworkPlayerInfo
import calcite.client.module.Category
import calcite.client.module.Module

internal object ExtraTab : Module(
    name = "ExtraTab",
    description = "Expands the player tab menu",
    category = Category.RENDER
) {
    private val tabSize by setting("Max Players", 265, 80..400, 5)

    @JvmStatic
    fun subList(list: List<NetworkPlayerInfo>, newList: List<NetworkPlayerInfo>): List<NetworkPlayerInfo> {
        return if (isDisabled) newList else list.subList(0, tabSize.coerceAtMost(list.size))
    }
}