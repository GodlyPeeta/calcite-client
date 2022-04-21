package calcite.client.module.modules.chat

import net.minecraft.network.play.server.SPacketChat
import calcite.client.event.events.PacketEvent
import calcite.client.manager.managers.FriendManager
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.text.MessageDetection
import calcite.client.util.text.MessageSendHelper.sendServerMessage
import calcite.event.listener.listener

internal object AutoTPA : Module(
    name = "AutoTPA",
    description = "Automatically accept or decline /TPAs",
    category = Category.CHAT
) {
    private val friends = setting("Always Accept Friends", true)
    private val mode = setting("Response", Mode.DENY)

    private enum class Mode {
        ACCEPT, DENY
    }

    init {
        listener<PacketEvent.Receive> {
            if (it.packet !is SPacketChat || MessageDetection.Other.TPA_REQUEST detectNot it.packet.chatComponent.unformattedText) return@listener

            /* I tested that getting the first word is compatible with chat timestamp, and it as, as this is Receive and chat timestamp is after Receive */
            val name = it.packet.chatComponent.unformattedText.split(" ")[0]

            when (mode.value) {
                Mode.ACCEPT -> sendServerMessage("/tpaccept $name")
                Mode.DENY -> {
                    if (friends.value && FriendManager.isFriend(name)) {
                        sendServerMessage("/tpaccept $name")
                    } else {
                        sendServerMessage("/tpdeny $name")
                    }
                }
            }
        }
    }
}