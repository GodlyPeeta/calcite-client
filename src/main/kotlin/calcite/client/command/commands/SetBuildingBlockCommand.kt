package calcite.client.command.commands

import net.minecraft.block.BlockAir
import calcite.client.command.ClientCommand
import calcite.client.module.modules.player.InventoryManager
import calcite.client.util.items.block
import calcite.client.util.items.id
import calcite.client.util.text.MessageSendHelper

// TODO: Remove once GUI has Block settings
object SetBuildingBlockCommand : ClientCommand(
    name = "setbuildingblock",
    description = "Set the default building block"
) {
    init {
        executeSafe {
            val heldItem = player.inventory.getCurrentItem()
            when {
                heldItem.isEmpty -> {
                    InventoryManager.buildingBlockID = 0
                    MessageSendHelper.sendChatMessage("Building block has been reset")
                }
                heldItem.item.block !is BlockAir -> {
                    InventoryManager.buildingBlockID = heldItem.item.id
                    MessageSendHelper.sendChatMessage("Building block has been set to ${heldItem.displayName}")
                }
                else -> {
                    MessageSendHelper.sendChatMessage("You are not holding a valid block")
                }
            }
        }
    }
}