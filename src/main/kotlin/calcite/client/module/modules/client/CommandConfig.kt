package calcite.client.module.modules.client

import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.CalciteMod
import calcite.client.event.events.ModuleToggleEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.TickTimer
import calcite.client.util.text.MessageSendHelper
import calcite.client.util.text.format
import calcite.event.listener.listener
import org.lwjgl.opengl.Display

internal object CommandConfig : Module(
    name = "CommandConfig",
    category = Category.CLIENT,
    description = "Configures client chat related stuff",
    showOnArray = false,
    alwaysEnabled = true
) {
    var prefix by setting("Prefix", ";", { false })
    val toggleMessages by setting("Toggle Messages", false)
    private val customTitle = setting("Window Title", true)

    private val timer = TickTimer()
    private val prevTitle = Display.getTitle()
    private const val title = "${calcite.client.CalciteMod.NAME} ${calcite.client.CalciteMod.VERSION_SIMPLE}"

    init {
        listener<ModuleToggleEvent> {
            if (!toggleMessages) return@listener

            MessageSendHelper.sendChatMessage(it.module.name +
                if (it.module.isEnabled) TextFormatting.RED format " disabled"
                else TextFormatting.GREEN format " enabled"
            )
        }

        listener<TickEvent.ClientTickEvent> {
            if (timer.tick(10000L)) {
                if (customTitle.value) Display.setTitle(title)
                else Display.setTitle(prevTitle)
            }
        }

        customTitle.listeners.add {
            timer.reset(-0xCAFEBABE)
        }
    }
}