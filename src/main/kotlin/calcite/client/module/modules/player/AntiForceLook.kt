package calcite.client.module.modules.player

import net.minecraft.network.play.server.SPacketPlayerPosLook
import calcite.client.event.events.PacketEvent
import calcite.client.mixin.extension.rotationPitch
import calcite.client.mixin.extension.rotationYaw
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.event.listener.listener

internal object AntiForceLook : Module(
    name = "AntiForceLook",
    category = Category.PLAYER,
    description = "Stops server packets from turning your head"
) {
    init {
        listener<PacketEvent.Receive> {
            if (it.packet !is SPacketPlayerPosLook || mc.player == null) return@listener
            it.packet.rotationYaw = mc.player.rotationYaw
            it.packet.rotationPitch = mc.player.rotationPitch
        }
    }
}