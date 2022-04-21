package calcite.client.module.modules.chat

import net.minecraft.network.play.server.SPacketChat
import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.event.events.PacketEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.TickTimer
import calcite.client.util.TimeUnit
import calcite.client.util.text.MessageDetection
import calcite.client.util.text.MessageSendHelper
import calcite.client.util.text.MessageSendHelper.sendServerMessage
import calcite.client.util.threads.safeListener
import calcite.event.listener.listener

internal object AutoReply : Module(
    name = "AutoReply",
    description = "Automatically reply to direct messages",
    category = Category.CHAT
) {
    private val customMessage = setting("Custom Message", false)
    private val customText = setting("Custom Text", "unchanged", { customMessage.value })

    private val timer = TickTimer(TimeUnit.SECONDS)

    init {
        listener<PacketEvent.Receive> {
            if (it.packet !is SPacketChat || MessageDetection.Direct.RECEIVE detect it.packet.chatComponent.unformattedText) return@listener
            if (customMessage.value) {
                sendServerMessage("/r " + customText.value)
            } else {
                sendServerMessage("/r I just automatically replied, thanks to KAMI Blue's AutoReply module!")
            }
        }

        safeListener<TickEvent.ClientTickEvent> {
            if (timer.tick(5L) && customMessage.value && customText.value.equals("unchanged", true)) {
                MessageSendHelper.sendWarningMessage("$chatName Warning: In order to use the custom $name, please change the CustomText setting in ClickGUI")
            }
        }
    }
}