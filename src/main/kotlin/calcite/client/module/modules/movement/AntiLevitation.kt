package calcite.client.module.modules.movement

import net.minecraft.init.MobEffects
import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.threads.safeListener

internal object AntiLevitation : Module(
    name = "AntiLevitation",
    description = "Removes levitation potion effect",
    category = Category.MOVEMENT
) {
    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (player.isPotionActive(MobEffects.LEVITATION)) {
                player.removeActivePotionEffect(MobEffects.LEVITATION)
            }
        }
    }
}