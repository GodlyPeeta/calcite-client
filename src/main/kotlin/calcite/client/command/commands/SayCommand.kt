package calcite.client.command.commands

import calcite.client.command.ClientCommand
import calcite.client.util.text.MessageSendHelper.sendServerMessage

object SayCommand : ClientCommand(
    name = "say",
    description = "Allows you to send any message, even with a prefix in it."
) {
    init {
        greedy("message") { messageArg ->
            executeSafe {
                sendServerMessage(messageArg.value.trim())
            }
        }
    }
}