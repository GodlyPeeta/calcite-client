package calcite.client.module.modules.render

import kotlinx.coroutines.launch
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import calcite.client.event.SafeClientEvent
import calcite.client.event.events.RenderWorldEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.TickTimer
import calcite.client.util.color.ColorHolder
import calcite.client.util.graphics.ESPRenderer
import calcite.client.util.graphics.GeometryMasks
import calcite.client.util.math.VectorUtils.distanceTo
import calcite.client.util.threads.defaultScope
import calcite.client.util.threads.safeListener

internal object VoidESP : Module(
    name = "VoidESP",
    description = "Highlights holes leading to the void",
    category = Category.RENDER
) {
    private val filled by setting("Filled", true)
    private val outline by setting("Outline", true)
    private val color by setting("Color", ColorHolder(148, 161, 255), false)
    private val aFilled by setting("Filled Alpha", 127, 0..255, 1)
    private val aOutline by setting("Outline Alpha", 255, 0..255, 1)
    private val renderMode by setting("Mode", Mode.BLOCK_HOLE)
    private val range by setting("Range", 8, 4..32, 1)

    @Suppress("UNUSED")
    private enum class Mode {
        BLOCK_HOLE, BLOCK_VOID, FLAT
    }

    private val renderer = ESPRenderer()
    private val timer = TickTimer()

    init {
        safeListener<RenderWorldEvent> {
            if (timer.tick(133L)) { // Avoid running this on a tick
                updateRenderer()
            }
            renderer.render(false)
        }
    }

    private fun SafeClientEvent.updateRenderer() {
        renderer.aFilled = if (filled) aFilled else 0
        renderer.aOutline = if (outline) aOutline else 0

        val color = color.clone()
        val side = if (renderMode != Mode.FLAT) GeometryMasks.Quad.ALL else GeometryMasks.Quad.DOWN

        defaultScope.launch {
            val cached = ArrayList<Triple<AxisAlignedBB, ColorHolder, Int>>()

            for (x in -range..range) for (z in -range..range) {
                val pos = BlockPos(player.posX + x, 0.0, player.posZ + z)
                if (player.distanceTo(pos) > range) continue
                if (!isVoid(pos)) continue

                val renderPos = if (renderMode == Mode.BLOCK_VOID) pos.down() else pos
                cached.add(Triple(AxisAlignedBB(renderPos), color, side))
            }

            renderer.replaceAll(cached, 1)
        }
    }

    private fun SafeClientEvent.isVoid(pos: BlockPos) = world.isAirBlock(pos)
        && world.isAirBlock(pos.up())
        && world.isAirBlock(pos.up().up())

}
