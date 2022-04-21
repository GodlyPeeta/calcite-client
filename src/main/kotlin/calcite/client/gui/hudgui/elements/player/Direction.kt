package calcite.client.gui.hudgui.elements.player

import calcite.client.event.SafeClientEvent
import calcite.client.gui.hudgui.LabelHud
import calcite.client.util.math.Direction

internal object Direction : LabelHud(
    name = "Direction",
    category = Category.PLAYER,
    description = "Direction of player facing to"
) {

    override fun SafeClientEvent.updateText() {
        val entity = mc.renderViewEntity ?: player
        val direction = Direction.fromEntity(entity)
        displayText.add(direction.displayName, secondaryColor)
        displayText.add("(${direction.displayNameXY})", primaryColor)
    }

}