package calcite.client.module.modules.movement

import calcite.client.mixin.client.entity.MixinEntity
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.module.modules.player.Scaffold
import calcite.client.util.BaritoneUtils
import calcite.client.util.EntityUtils.flooredPosition
import calcite.client.util.Wrapper
import calcite.client.util.math.VectorUtils.toVec3d
import calcite.client.util.threads.runSafeR

/**
 * @see MixinEntity.moveInvokeIsSneakingPre
 * @see MixinEntity.moveInvokeIsSneakingPost
 */
internal object SafeWalk : Module(
    name = "SafeWalk",
    category = Category.MOVEMENT,
    description = "Keeps you from walking off edges"
) {
    private val checkFallDist by setting("Check Fall Distance", true, description = "Check fall distance from edge")

    init {
        onToggle {
            BaritoneUtils.settings?.assumeSafeWalk?.value = it
        }
    }

    @JvmStatic
    fun shouldSafewalk(entityID: Int) =
        (Wrapper.player?.let { !it.isSneaking && it.entityId == entityID } ?: false)
            && (isEnabled || Scaffold.isEnabled && Scaffold.safeWalk)
            && (!checkFallDist && !BaritoneUtils.isPathing || !isEdgeSafe)

    @JvmStatic
    fun setSneaking(state: Boolean) {
        Wrapper.player?.movementInput?.sneak = state
    }

    private val isEdgeSafe: Boolean
        get() = runSafeR {
            val pos = player.flooredPosition.toVec3d(0.5, 0.0, 0.5)
            world.rayTraceBlocks(
                pos,
                pos.subtract(0.0, 3.1, 0.0),
                true,
                true,
                false
            ) != null
        } ?: false
}