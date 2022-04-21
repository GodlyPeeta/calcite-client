package calcite.client.module.modules.misc

import club.minnced.discord.rpc.DiscordEventHandlers
import club.minnced.discord.rpc.DiscordRichPresence
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.CalciteMod
import calcite.client.event.events.ShutdownEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.client.util.InfoCalculator
import calcite.client.util.InfoCalculator.speed
import calcite.client.util.TickTimer
import calcite.client.util.TimeUnit
import calcite.client.util.TpsCalculator
import calcite.client.util.math.CoordinateConverter.asString
import calcite.client.util.math.VectorUtils.toBlockPos
import calcite.client.util.text.MessageSendHelper
import calcite.client.util.threads.BackgroundJob
import calcite.client.util.threads.BackgroundScope
import calcite.client.util.threads.runSafeR
import calcite.client.util.threads.safeListener
import calcite.commons.utils.MathUtils
import calcite.event.listener.listener

internal object DiscordRPC : Module(
    name = "DiscordRPC",
    category = Category.MISC,
    description = "Discord Rich Presence",
    enabledByDefault = true
) {
    private val line1Left by setting("Line 1 Left", LineInfo.VERSION) // details left
    private val line1Right by setting("Line 1 Right", LineInfo.NONE) // details right
    private val line2Left by setting("Line 2 Left", LineInfo.SERVER_IP) // state left
    private val line2Right by setting("Line 2 Right", LineInfo.USERNAME) // state right
    private val coordsConfirm by setting("Coords Confirm", false, { showCoordsConfirm() })

    private enum class LineInfo {
        VERSION, WORLD, DIMENSION, USERNAME, HEALTH, HUNGER, SERVER_IP, COORDS, SPEED, HELD_ITEM, FPS, TPS, NONE
    }

    private val presence = DiscordRichPresence()
    private val rpc = club.minnced.discord.rpc.DiscordRPC.INSTANCE
    private var connected = false
    private val timer = TickTimer(TimeUnit.SECONDS)
    private val job = BackgroundJob("Discord RPC", 5000L) { updateRPC() }

    init {
        onEnable {
            start()
        }

        onDisable {
            end()
        }

        safeListener<TickEvent.ClientTickEvent> {
            if (showCoordsConfirm() && !coordsConfirm && timer.tick(10L)) {
                MessageSendHelper.sendWarningMessage("$chatName Warning: In order to use the coords option please enable the coords confirmation option. " +
                    "This will display your coords on the discord rpc. " +
                    "Do NOT use this if you do not want your coords displayed")
            }
        }

        listener<ShutdownEvent> {
            end()
        }
    }

    private fun start() {
        if (connected) return

        calcite.client.CalciteMod.LOG.info("Starting Discord RPC")
        connected = true
        rpc.Discord_Initialize(calcite.client.CalciteMod.APP_ID, DiscordEventHandlers(), true, "")
        //presence.startTimestamp = System.currentTimeMillis() / 1000L

        BackgroundScope.launchLooping(job)

        calcite.client.CalciteMod.LOG.info("Discord RPC initialised successfully")
    }

    private fun end() {
        if (!connected) return

        calcite.client.CalciteMod.LOG.info("Shutting down Discord RPC...")
        BackgroundScope.cancel(job)
        connected = false
        rpc.Discord_Shutdown()
    }

    private fun showCoordsConfirm(): Boolean {
        return line1Left == LineInfo.COORDS
            || line2Left == LineInfo.COORDS
            || line1Right == LineInfo.COORDS
            || line2Right == LineInfo.COORDS
    }

    private fun updateRPC() {
        presence.details = getLine(line1Left) + getSeparator(0) + getLine(line1Right)
        presence.state = getLine(line2Left) + getSeparator(1) + getLine(line2Right)
        rpc.Discord_UpdatePresence(presence)
    }

    private fun getLine(line: LineInfo): String {
        return when (line) {
            LineInfo.VERSION -> {
                calcite.client.CalciteMod.VERSION_SIMPLE
            }
            LineInfo.WORLD -> {
                when {
                    mc.isIntegratedServerRunning -> "Singleplayer"
                    mc.currentServerData != null -> "Multiplayer"
                    else -> "Main Menu"
                }
            }
            LineInfo.DIMENSION -> {
                InfoCalculator.dimension()
            }
            LineInfo.USERNAME -> {
                mc.session.username
            }
            LineInfo.HEALTH -> {
                if (mc.player != null) "${mc.player.health.toInt()} HP"
                else "No HP"
            }
            LineInfo.HUNGER -> {
                if (mc.player != null) "${mc.player.foodStats.foodLevel} hunger"
                else "No Hunger"
            }
            LineInfo.SERVER_IP -> {
                InfoCalculator.getServerType()
            }
            LineInfo.COORDS -> {
                if (mc.player != null && coordsConfirm) "(${mc.player.positionVector.toBlockPos().asString()})"
                else "No Coords"
            }
            LineInfo.SPEED -> {
                runSafeR {
                    "${"%.1f".format(speed())} m/s"
                } ?: "No Speed"
            }
            LineInfo.HELD_ITEM -> {
                "Holding ${mc.player?.heldItemMainhand?.displayName ?: "Air"}" // Holding air meme
            }
            LineInfo.FPS -> {
                "${Minecraft.getDebugFPS()} FPS"
            }
            LineInfo.TPS -> {
                if (mc.player != null) "${MathUtils.round(TpsCalculator.tickRate, 1)} tps"
                else "No Tps"
            }
            else -> {
                " "
            }
        }
    }

    private fun getSeparator(line: Int): String {
        return if (line == 0) {
            if (line1Left == LineInfo.NONE || line1Right == LineInfo.NONE) " " else " | "
        } else {
            if (line2Left == LineInfo.NONE || line2Right == LineInfo.NONE) " " else " | "
        }
    }

    init {
        presence.largeImageKey = "calcite"
        presence.smallImageKey = "minecraftlogo"
        presence.largeImageText = "https://github.com/GodlyPeeta/calcite-client"
    }
}
