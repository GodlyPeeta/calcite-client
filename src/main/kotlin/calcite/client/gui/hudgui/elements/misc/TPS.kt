package calcite.client.gui.hudgui.elements.misc

import calcite.client.event.SafeClientEvent
import calcite.client.gui.hudgui.LabelHud
import calcite.client.util.CircularArray
import calcite.client.util.TpsCalculator

internal object TPS : LabelHud(
    name = "TPS",
    category = Category.MISC,
    description = "Server TPS"
) {

    private val mspt = setting("Use milliseconds", false, description = "Use milliseconds per tick instead of ticks per second")

    // buffered TPS readings to add some fluidity to the TPS HUD element
    private val tpsBuffer = CircularArray.create(20, 20f)

    override fun SafeClientEvent.updateText() {
        tpsBuffer.add(TpsCalculator.tickRate)
        val avg = tpsBuffer.average()

        if (mspt.value) {
            // If the Value returns Zero, it reads "Infinity mspt"
            if (avg == 0.00f) {
                displayText.add("%.2f".format(0.00f), primaryColor)
            } else {
                displayText.add("%.2f".format(1000 / avg), primaryColor)
            }

            displayText.add("mspt", secondaryColor)
        } else {
            displayText.add("%.2f".format(avg), primaryColor)
            displayText.add("tps", secondaryColor)
        }
    }

}
