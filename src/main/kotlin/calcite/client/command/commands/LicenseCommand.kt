package calcite.client.command.commands

import calcite.client.command.ClientCommand
import calcite.client.util.text.MessageSendHelper

object LicenseCommand : ClientCommand(
    name = "license",
    description = "Information about KAMI Blue's license"
) {
    init {
        execute {
            MessageSendHelper.sendChatMessage("You can view KAMI Blue's &7client&f License (LGPLv3) at &9https://calcite.org/license")
        }
    }
}