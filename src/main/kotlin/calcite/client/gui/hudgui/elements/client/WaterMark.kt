package calcite.client.gui.hudgui.elements.client

import calcite.client.CalciteMod
import calcite.client.event.SafeClientEvent
import calcite.client.gui.hudgui.LabelHud
import calcite.client.util.graphics.VertexHelper
import org.lwjgl.opengl.GL11.glScalef

internal object WaterMark : LabelHud(
    name = "Watermark",
    category = Category.CLIENT,
    description = "KAMI Blue watermark",
    enabledByDefault = true
) {

    override val hudWidth: Float get() = (displayText.getWidth() + 2.0f) / scale
    override val hudHeight: Float get() = displayText.getHeight(2) / scale

    override val closeable: Boolean get() = true

    override fun SafeClientEvent.updateText() {
        displayText.add(calcite.client.CalciteMod.NAME, primaryColor)
        displayText.add(calcite.client.CalciteMod.VERSION_SIMPLE, secondaryColor)
    }

    override fun renderHud(vertexHelper: VertexHelper) {
        val reversedScale = 1.0f / scale
        glScalef(reversedScale, reversedScale, reversedScale)
        super.renderHud(vertexHelper)
    }

    init {
        posX = 0.0f
        posY = 0.0f
    }
}