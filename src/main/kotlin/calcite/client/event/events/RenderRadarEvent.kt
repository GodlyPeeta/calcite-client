package calcite.client.event.events

import calcite.client.event.Event
import calcite.client.util.graphics.VertexHelper

class RenderRadarEvent(
    val vertexHelper: VertexHelper,
    val radius: Float,
    val scale: Float
) : Event