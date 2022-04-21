package calcite.client.module.modules.player

import net.minecraft.network.play.client.CPacketConfirmTeleport
import calcite.client.event.events.PacketEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.threads.runSafe
import calcite.event.listener.listener

internal object PortalGodMode : Module(
    name = "PortalGodMode",
    category = Category.PLAYER,
    description = "Don't take damage in portals"
) {
    private val instantTeleport by setting("Instant Teleport", true)

    private var packet: CPacketConfirmTeleport? = null

    init {
        onEnable {
            packet = null
        }

        onDisable {
            runSafe {
                if (instantTeleport) packet?.let {
                    connection.sendPacket(it)
                }
            }
        }

        listener<PacketEvent.Send> {
            if (it.packet !is CPacketConfirmTeleport) return@listener
            it.cancel()
            packet = it.packet
        }
    }
}