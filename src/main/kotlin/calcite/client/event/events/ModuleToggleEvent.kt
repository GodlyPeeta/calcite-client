package calcite.client.event.events

import calcite.client.event.Event
import calcite.client.module.AbstractModule

class ModuleToggleEvent internal constructor(val module: AbstractModule) : Event