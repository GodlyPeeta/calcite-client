package calcite.client

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import calcite.client.event.ForgeEventProcessor
import calcite.client.gui.mc.KamiGuiUpdateNotification
import calcite.client.util.ConfigUtils
import calcite.client.util.threads.BackgroundScope
import java.io.File

@Mod(
    modid = CalciteMod.ID,
    name = CalciteMod.NAME,
    version = CalciteMod.VERSION
)
class CalciteMod {

    companion object {
        const val NAME = "Calcite"
        const val ID = "calcite"
        const val DIRECTORY = "calciteclient/"

        const val VERSION = "Pre Pre Pre Pre alpha - dev" // Used for debugging. R.MM.DD-hash format.
        const val VERSION_SIMPLE = "0.0.1 Pre Pre Pre Pre alpha - dev" // Shown to the user. R.MM.DD[-beta] format.
        const val VERSION_MAJOR = "0.0.1" // Used for update checking. RR.MM.01 format.

        const val APP_ID = "966161396406583307"

        const val GITHUB_LINK = "https://github.com/GodlyPeeta/calcite-client"
        const val WEBSITE_LINK = "https://github.com/GodlyPeeta/calcite-client"

        val LOG: Logger = LogManager.getLogger(NAME)

        var ready: Boolean = false; private set
    }

    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        val directory = File(DIRECTORY)
        if (!directory.exists()) directory.mkdir()

        KamiGuiUpdateNotification.updateCheck()
        LoaderWrapper.preLoadAll()
    }

    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        LOG.info("Initializing $NAME $VERSION")

        LoaderWrapper.loadAll()

        MinecraftForge.EVENT_BUS.register(ForgeEventProcessor)

        ConfigUtils.moveAllLegacyConfigs()
        ConfigUtils.loadAll()

        BackgroundScope.start()

        LOG.info("$NAME initialized!")
    }

    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        ready = true
    }
}