package calcite.client.event.events

import calcite.client.event.Event
import calcite.client.event.KamiEventBus
import calcite.client.event.SingletonEvent

/**
 * Posted at the return of when Baritone's Settings are initialized.
 */
object BaritoneSettingsInitEvent : Event, SingletonEvent(KamiEventBus)