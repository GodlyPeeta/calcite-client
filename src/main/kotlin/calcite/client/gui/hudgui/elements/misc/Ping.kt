package calcite.client.gui.hudgui.elements.misc

import calcite.client.event.SafeClientEvent
import calcite.client.gui.hudgui.LabelHud
import calcite.client.util.InfoCalculator

internal object Ping : LabelHud(
    name = "Ping",
    category = Category.MISC,
    description = "Delay between client and server"
) {

    override fun SafeClientEvent.updateText() {
        displayText.add(InfoCalculator.ping().toString(), primaryColor)
        displayText.add("ms", secondaryColor)
    }

}