package calcite.client.event.events

import net.minecraft.util.math.Vec3d
import calcite.client.event.Cancellable
import calcite.client.event.Event
import calcite.client.event.IMultiPhase
import calcite.client.event.Phase
import calcite.client.manager.managers.PlayerPacketManager
import calcite.client.util.math.Vec2f
import calcite.commons.extension.next

class OnUpdateWalkingPlayerEvent private constructor(
    moving: Boolean,
    rotating: Boolean,
    position: Vec3d,
    rotation: Vec2f,
    override val phase: Phase
) : Event, IMultiPhase<OnUpdateWalkingPlayerEvent>, Cancellable() {

    var position = position; private set
    var rotation = rotation; private set

    var moving = moving
        @JvmName("isMoving") get
        private set

    var rotating = rotating
        @JvmName("isRotating") get
        private set

    var cancelAll = false; private set

    constructor(moving: Boolean, rotating: Boolean, position: Vec3d, rotation: Vec2f) : this(moving, rotating, position, rotation, Phase.PRE)

    override fun nextPhase(): OnUpdateWalkingPlayerEvent {
        return OnUpdateWalkingPlayerEvent(moving, rotating, position, rotation, phase.next())
    }

    fun apply(packet: PlayerPacketManager.Packet) {
        cancel()

        packet.moving?.let { moving ->
            if (moving) packet.position?.let { this.position = it }
            this.moving = moving
        }

        packet.rotating?.let { rotating ->
            if (rotating) packet.rotation?.let { this.rotation = it }
            this.rotating = rotating
        }

        this.cancelAll = packet.cancelAll
    }

}