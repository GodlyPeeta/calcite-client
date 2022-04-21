package calcite.client.module.modules.movement

import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.TickTimer
import calcite.client.util.TimeUnit
import calcite.client.util.threads.safeListener

internal object AutoJump : Module(
    name = "AutoJump",
    category = Category.MOVEMENT,
    description = "Automatically jumps if possible"
) {
    private val delay = setting("Tick Delay", 10, 0..40, 1)

    private val timer = TickTimer(TimeUnit.TICKS)

    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (player.isInWater || player.isInLava) player.motionY = 0.1
            else if (player.onGround && timer.tick(delay.value.toLong())) player.jump()
        }
    }
}