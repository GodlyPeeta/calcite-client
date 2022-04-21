package calcite.client.event.events

import calcite.client.event.Cancellable
import calcite.client.event.Event
import calcite.client.event.ICancellable
import calcite.client.event.ProfilerEvent

class PlayerTravelEvent : Event, ICancellable by Cancellable(), ProfilerEvent {
    override val profilerName: String = "kbPlayerTravel"
}