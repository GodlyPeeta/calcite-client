package calcite.client.module.modules.chat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.CalciteMod
import calcite.client.event.events.ConnectionEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.MovementUtils.isMoving
import calcite.client.util.text.MessageDetection
import calcite.client.util.text.MessageSendHelper
import calcite.client.util.text.MessageSendHelper.sendServerMessage
import calcite.client.util.threads.defaultScope
import calcite.client.util.threads.safeListener
import calcite.event.listener.listener
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

internal object LoginMessage : Module(
    name = "LoginMessage",
    description = "Sends a given message(s) to public chat on login.",
    category = Category.CHAT,
    showOnArray = false,
    modulePriority = 150
) {
    private val sendAfterMoving by setting("Send After Moving", false, description = "Wait until you have moved after logging in")

    private val file = File(calcite.client.CalciteMod.DIRECTORY + "loginmsg.txt")
    private val loginMessages = CopyOnWriteArrayList<String>()
    private var sent = false
    private var moved = false

    init {
        onEnable {
            if (file.exists()) {
                defaultScope.launch(Dispatchers.IO) {
                    try {
                        file.forEachLine {
                            if (it.isNotBlank()) loginMessages.add(it.trim())
                        }
                        MessageSendHelper.sendChatMessage("$chatName Loaded ${loginMessages.size} login messages!")
                    } catch (e: Exception) {
                        MessageSendHelper.sendErrorMessage("$chatName Failed loading login messages, $e")
                        disable()
                    }
                }
            } else {
                file.createNewFile()
                MessageSendHelper.sendErrorMessage("$chatName Login Messages file not found!" +
                    ", please add them in the &7loginmsg.txt&f under the &7.minecraft/calcite&f directory.")
                disable()
            }
        }

        onDisable {
            loginMessages.clear()
        }

        listener<ConnectionEvent.Disconnect> {
            sent = false
            moved = false
        }

        safeListener<TickEvent.ClientTickEvent> { event ->
            if (event.phase != TickEvent.Phase.END) return@safeListener

            if (!sent && (!sendAfterMoving || moved)) {
                for (message in loginMessages) {
                    if (MessageDetection.Command.KAMI_BLUE detect message) {
                        MessageSendHelper.sendKamiCommand(message)
                    } else {
                        sendServerMessage(message)
                    }
                }

                sent = true
            }

            if (!moved) moved = player.isMoving
        }
    }
}
