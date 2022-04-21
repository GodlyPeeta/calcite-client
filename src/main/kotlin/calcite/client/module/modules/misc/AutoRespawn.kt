package calcite.client.module.modules.misc

import net.minecraft.client.gui.GuiGameOver
import calcite.client.event.events.GuiEvent
import calcite.client.manager.managers.WaypointManager
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.InfoCalculator
import calcite.client.util.math.CoordinateConverter.asString
import calcite.client.util.text.MessageSendHelper
import calcite.event.listener.listener

internal object AutoRespawn : Module(
    name = "AutoRespawn",
    description = "Automatically respawn after dying",
    category = Category.MISC
) {
    private val respawn = setting("Respawn", true)
    private val deathCoords = setting("Save Death Coords", true)
    private val antiGlitchScreen = setting("Anti Glitch Screen", true)

    init {
        listener<GuiEvent.Displayed> {
            if (it.screen !is GuiGameOver) return@listener

            if (deathCoords.value && mc.player.health <= 0) {
                WaypointManager.add("Death - " + InfoCalculator.getServerType())
                MessageSendHelper.sendChatMessage("You died at ${mc.player.position.asString()}")
            }

            if (respawn.value || antiGlitchScreen.value && mc.player.health > 0) {
                mc.player.respawnPlayer()
                mc.displayGuiScreen(null)
            }
        }
    }
}