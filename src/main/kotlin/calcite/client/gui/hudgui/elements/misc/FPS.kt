package calcite.client.gui.hudgui.elements.misc

import net.minecraft.client.Minecraft
import calcite.client.event.SafeClientEvent
import calcite.client.event.events.RunGameLoopEvent
import calcite.client.gui.hudgui.LabelHud
import calcite.client.util.CircularArray
import calcite.client.util.graphics.AnimationUtils
import calcite.event.listener.listener
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal object FPS : LabelHud(
    name = "FPS",
    category = Category.MISC,
    description = "Frame per second in game"
) {

    private val showAverage = setting("Show Average", true)
    private val showMin = setting("Show Min", false)
    private val showMax = setting("Show Max", false)

    private var updateTime = 0L
    private var prevFps = 0
    private var currentFps = 0

    private val longFps = CircularArray.create<Int>(10)

    private var prevAvgFps = 0
    private var currentAvgFps = 0

    init {
        listener<RunGameLoopEvent.End> {
            val newFps = Minecraft.getDebugFPS()
            if (newFps == currentFps) return@listener

            prevFps = currentFps
            currentFps = newFps

            longFps.add(newFps)

            prevAvgFps = currentAvgFps
            currentAvgFps = longFps.average().roundToInt()
            updateTime = System.currentTimeMillis()
        }
    }

    override fun SafeClientEvent.updateText() {
        val deltaTime = AnimationUtils.toDeltaTimeFloat(updateTime) / 1000.0f
        val fps = (prevFps + (currentFps - prevFps) * deltaTime).roundToInt()
        val avg = (prevAvgFps + (currentAvgFps - prevAvgFps) * deltaTime).roundToInt()

        var min = 6969
        var max = 0
        for (value in longFps) {
            if (value != 0) min = min(value, min)
            max = max(value, max)
        }

        displayText.add("FPS", secondaryColor)
        displayText.add(fps.toString(), primaryColor)

        if (showAverage.value) {
            displayText.add("AVG", secondaryColor)
            displayText.add(avg.toString(), primaryColor)
        }

        if (showMin.value) {
            displayText.add("MIN", secondaryColor)
            displayText.add(min.toString(), primaryColor)
        }

        if (showMax.value) {
            displayText.add("MAX", secondaryColor)
            displayText.add(max.toString(), primaryColor)
        }
    }

}