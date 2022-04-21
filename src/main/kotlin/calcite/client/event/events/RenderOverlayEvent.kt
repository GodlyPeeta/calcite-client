package calcite.client.event.events

import calcite.client.event.Event
import calcite.client.event.ProfilerEvent

class RenderOverlayEvent : Event, ProfilerEvent {
    override val profilerName: String = "kbRender2D"
}