package calcite.client.module.modules.misc

import calcite.client.mixin.client.world.MixinWorld
import calcite.client.module.Category
import calcite.client.module.Module

/**
 * @see MixinWorld.getThunderStrengthHead
 * @see MixinWorld.getRainStrengthHead
 */
internal object AntiWeather : Module(
    name = "AntiWeather",
    description = "Removes rain and thunder from your world",
    category = Category.MISC
)
