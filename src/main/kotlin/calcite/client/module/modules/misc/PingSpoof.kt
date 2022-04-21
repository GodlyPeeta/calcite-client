package calcite.client.module.modules.misc

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.server.SPacketKeepAlive
import calcite.client.event.events.PacketEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.threads.defaultScope
import calcite.client.util.threads.onMainThreadSafe
import calcite.event.listener.listener

internal object PingSpoof : Module(
    name = "PingSpoof",
    category = Category.MISC,
    description = "Cancels or adds delay to your ping packets"
) {
    private val delay = setting("Delay", 100, 0..2000, 25)

    init {
        listener<PacketEvent.Receive> {
            if (it.packet is SPacketKeepAlive) {
                it.cancel()
                defaultScope.launch {
                    delay(delay.value.toLong())
                    onMainThreadSafe {
                        connection.sendPacket(CPacketKeepAlive(it.packet.id))
                    }
                }
            }
        }
    }
}
