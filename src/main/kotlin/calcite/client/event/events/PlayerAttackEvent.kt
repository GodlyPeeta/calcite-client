package calcite.client.event.events

import net.minecraft.entity.Entity
import calcite.client.event.Cancellable
import calcite.client.event.Event

class PlayerAttackEvent(val entity: Entity) : Event, Cancellable()