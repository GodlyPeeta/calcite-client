package calcite.client.gui.hudgui

import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.event.SafeClientEvent
import calcite.client.setting.configs.AbstractConfig
import calcite.client.util.graphics.VertexHelper
import calcite.client.util.graphics.font.TextComponent
import calcite.client.util.math.Vec2d
import calcite.client.util.threads.safeAsyncListener
import calcite.commons.interfaces.Nameable

abstract class AbstractLabelHud(
    name: String,
    alias: Array<String>,
    category: Category,
    description: String,
    alwaysListening: Boolean,
    enabledByDefault: Boolean,
    config: AbstractConfig<out Nameable>,
) : AbstractHudElement(name, alias, category, description, alwaysListening, enabledByDefault, config) {

    override val hudWidth: Float get() = displayText.getWidth() + 2.0f
    override val hudHeight: Float get() = displayText.getHeight(2)

    protected val displayText = TextComponent()

    init {
        safeAsyncListener<TickEvent.ClientTickEvent> {
            if (it.phase != TickEvent.Phase.END) return@safeAsyncListener
            displayText.clear()
            updateText()
        }
    }

    abstract fun SafeClientEvent.updateText()

    override fun renderHud(vertexHelper: VertexHelper) {
        super.renderHud(vertexHelper)

        val textPosX = width * dockingH.multiplier / scale - dockingH.offset
        val textPosY = height * dockingV.multiplier / scale

        displayText.draw(
            Vec2d(textPosX.toDouble(), textPosY.toDouble()),
            horizontalAlign = dockingH,
            verticalAlign = dockingV
        )
    }

}