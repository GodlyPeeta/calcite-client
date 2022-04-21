package calcite.client.module.modules.misc

import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.manager.managers.WaypointManager
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.InfoCalculator
import calcite.client.util.TickTimer
import calcite.client.util.TimeUnit
import calcite.client.util.math.CoordinateConverter.asString
import calcite.client.util.math.VectorUtils.toBlockPos
import calcite.client.util.text.MessageSendHelper
import calcite.client.util.threads.safeListener

internal object CoordsLog : Module(
    name = "CoordsLog",
    description = "Automatically logs your coords, based on actions",
    category = Category.MISC
) {
    private val saveOnDeath = setting("Save On Death", true)
    private val autoLog = setting("Automatically Log", false)
    private val delay = setting("Delay", 15, 1..60, 1)

    private var previousCoord: String? = null
    private var savedDeath = false
    private var timer = TickTimer(TimeUnit.SECONDS)

    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (autoLog.value && timer.tick(delay.value.toLong())) {
                val currentCoord = player.positionVector.toBlockPos().asString()

                if (currentCoord != previousCoord) {
                    WaypointManager.add("autoLogger")
                    previousCoord = currentCoord
                }
            }

            if (saveOnDeath.value) {
                savedDeath = if (player.isDead || player.health <= 0.0f) {
                    if (!savedDeath) {
                        val deathPoint = WaypointManager.add("Death - " + InfoCalculator.getServerType()).pos
                        MessageSendHelper.sendChatMessage("You died at ${deathPoint.x}, ${deathPoint.y}, ${deathPoint.z}")
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

}
