package calcite.client.module.modules.render

import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.scoreboard.ScorePlayerTeam
import calcite.client.manager.managers.FriendManager
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.color.EnumTextColor
import calcite.client.util.text.format

internal object TabFriends : Module(
    name = "TabFriends",
    description = "Highlights friends in the tab menu",
    category = Category.RENDER,
    showOnArray = false
) {
    private val color = setting("Color", EnumTextColor.GREEN)

    @JvmStatic
    fun getPlayerName(info: NetworkPlayerInfo): String {
        val name = info.displayName?.formattedText
            ?: ScorePlayerTeam.formatPlayerName(info.playerTeam, info.gameProfile.name)

        return if (FriendManager.isFriend(name)) {
            color.value format name
        } else {
            name
        }
    }
}