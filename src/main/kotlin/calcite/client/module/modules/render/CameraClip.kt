package calcite.client.module.modules.render

import calcite.client.mixin.client.render.MixinEntityRenderer
import calcite.client.module.Category
import calcite.client.module.Module

/**
 * @see MixinEntityRenderer.rayTraceBlocks
 */
internal object CameraClip : Module(
    name = "CameraClip",
    category = Category.RENDER,
    description = "Allows your 3rd person camera to pass through blocks",
    showOnArray = false
)
