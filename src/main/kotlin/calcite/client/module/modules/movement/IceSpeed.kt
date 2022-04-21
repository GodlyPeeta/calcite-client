package calcite.client.module.modules.movement

import net.minecraft.init.Blocks
import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.threads.safeListener

internal object IceSpeed : Module(
    name = "IceSpeed",
    description = "Changes how slippery ice is",
    category = Category.MOVEMENT
) {
    private val slipperiness by setting("Slipperiness", 0.4f, 0.1f..1.0f, 0.01f)

    init {
        safeListener<TickEvent.ClientTickEvent> {
            Blocks.ICE.setDefaultSlipperiness(slipperiness)
            Blocks.PACKED_ICE.setDefaultSlipperiness(slipperiness)
            Blocks.FROSTED_ICE.setDefaultSlipperiness(slipperiness)
        }

        onDisable {
            Blocks.ICE.setDefaultSlipperiness(0.98f)
            Blocks.PACKED_ICE.setDefaultSlipperiness(0.98f)
            Blocks.FROSTED_ICE.setDefaultSlipperiness(0.98f)
        }
    }
}