package calcite.client.module.modules.player

import net.minecraft.network.play.client.CPacketCloseWindow
import calcite.client.event.events.PacketEvent
import calcite.client.mixin.extension.windowID
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.event.listener.listener

internal object XCarry : Module(
    name = "XCarry",
    category = Category.PLAYER,
    description = "Store items in crafting slots"
) {
    init {
        listener<PacketEvent.Send> {
            if (it.packet is CPacketCloseWindow && it.packet.windowID == 0) {
                it.cancel()
            }
        }
    }
}