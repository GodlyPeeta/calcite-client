package calcite.client.plugin.api

import calcite.commons.interfaces.Nameable

interface IPluginClass : Nameable {
    val pluginMain: Plugin
}