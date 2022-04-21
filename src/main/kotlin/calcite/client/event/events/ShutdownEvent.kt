package calcite.client.event.events

import calcite.client.event.Event
import calcite.client.event.KamiEventBus
import calcite.client.event.SingletonEvent

object ShutdownEvent : Event, SingletonEvent(KamiEventBus)