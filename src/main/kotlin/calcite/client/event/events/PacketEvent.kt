package calcite.client.event.events

import net.minecraft.network.Packet
import calcite.client.event.Cancellable
import calcite.client.event.Event
import calcite.client.event.ICancellable

abstract class PacketEvent(val packet: Packet<*>) : Event, ICancellable by Cancellable() {
    class Receive(packet: Packet<*>) : PacketEvent(packet)
    class PostReceive(packet: Packet<*>) : PacketEvent(packet)
    class Send(packet: Packet<*>) : PacketEvent(packet)
    class PostSend(packet: Packet<*>) : PacketEvent(packet)
}