package calcite.client.module.modules.misc

import net.minecraft.init.SoundEvents
import net.minecraft.network.play.server.SPacketSoundEffect
import net.minecraft.util.SoundCategory
import calcite.client.event.events.PacketEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.event.listener.listener

internal object NoSoundLag : Module(
    name = "NoSoundLag",
    category = Category.MISC,
    description = "Prevents lag caused by sound machines"
) {
    init {
        listener<PacketEvent.Receive> {
            if (it.packet !is SPacketSoundEffect) return@listener
            if (it.packet.category == SoundCategory.PLAYERS && it.packet.sound === SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                it.cancel()
            }
        }
    }
}