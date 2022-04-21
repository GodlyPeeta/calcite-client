package calcite.client.module.modules.player

import calcite.client.mixin.client.network.MixinNetworkManager
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.text.MessageSendHelper.sendWarningMessage

/**
 * @see MixinNetworkManager
 */
internal object NoPacketKick : Module(
    name = "NoPacketKick",
    category = Category.PLAYER,
    description = "Suppress network exceptions and prevent getting kicked",
    showOnArray = false
) {
    @JvmStatic
    fun sendWarning(throwable: Throwable) {
        sendWarningMessage("$chatName Caught exception - \"$throwable\" check log for more info.")
        throwable.printStackTrace()
    }
}
