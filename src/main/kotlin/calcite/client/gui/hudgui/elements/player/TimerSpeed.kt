package calcite.client.gui.hudgui.elements.player

import calcite.client.event.SafeClientEvent
import calcite.client.gui.hudgui.LabelHud
import calcite.client.manager.managers.TimerManager
import calcite.commons.utils.MathUtils

internal object TimerSpeed : LabelHud(
    name = "TimerSpeed",
    category = Category.PLAYER,
    description = "Client side timer speed"
) {

    override fun SafeClientEvent.updateText() {
        val timerSpeed = MathUtils.round(50.0f / TimerManager.tickLength, 2)

        displayText.add("%.2f".format(timerSpeed), primaryColor)
        displayText.add("x", secondaryColor)
    }

}