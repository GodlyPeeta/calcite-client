package calcite.client.util

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.WorldClient
import calcite.client.CalciteMod
import calcite.client.event.events.ShutdownEvent
import calcite.client.util.ConfigUtils.saveAll

object Wrapper {
    @JvmStatic
    val minecraft: Minecraft
        get() = Minecraft.getMinecraft()

    @JvmStatic
    val player: EntityPlayerSP?
        get() = minecraft.player

    @JvmStatic
    val world: WorldClient?
        get() = minecraft.world

    @JvmStatic
    fun saveAndShutdown() {
        if (!calcite.client.CalciteMod.ready) return

        ShutdownEvent.post()
        saveAll()
    }
}