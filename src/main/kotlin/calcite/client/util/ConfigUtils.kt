package calcite.client.util

import calcite.client.CalciteMod
import calcite.client.manager.managers.FriendManager
import calcite.client.manager.managers.MacroManager
import calcite.client.manager.managers.UUIDManager
import calcite.client.manager.managers.WaypointManager
import calcite.client.setting.ConfigManager
import calcite.client.setting.GenericConfig
import calcite.client.setting.ModuleConfig
import calcite.client.setting.configs.IConfig
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files

object ConfigUtils {

    fun loadAll(): Boolean {
        var success = ConfigManager.loadAll()
        success = MacroManager.loadMacros() && success // Macro
        success = WaypointManager.loadWaypoints() && success // Waypoint
        success = FriendManager.loadFriends() && success // Friends
        success = UUIDManager.load() && success // UUID Cache

        return success
    }

    fun saveAll(): Boolean {
        var success = ConfigManager.saveAll()
        success = MacroManager.saveMacros() && success // Macro
        success = WaypointManager.saveWaypoints() && success // Waypoint
        success = FriendManager.saveFriends() && success // Friends
        success = UUIDManager.save() && success // UUID Cache

        return success
    }

    fun isPathValid(path: String): Boolean {
        return try {
            File(path).canonicalPath
            true
        } catch (e: Throwable) {
            false
        }
    }

    fun fixEmptyJson(file: File, isArray: Boolean = false) {
        var empty = false

        if (!file.exists()) {
            file.createNewFile()
            empty = true
        } else if (file.length() <= 8) {
            val string = file.readText()
            empty = string.isBlank() || string.all {
                it == '[' || it == ']' || it == '{' || it == '}' || it == ' ' || it == '\n' || it == '\r'
            }
        }

        if (empty) {
            try {
                FileWriter(file, false).use {
                    it.write(if (isArray) "[]" else "{}")
                }
            } catch (exception: IOException) {
                calcite.client.CalciteMod.LOG.warn("Failed fixing empty json", exception)
            }
        }
    }

    // TODO: Introduce a version helper for CalciteMod.BUILD_NUMBER for version-specific configs. This should be theoritically fine for now
    fun moveAllLegacyConfigs() {
        moveLegacyConfig("calcite/generic.json", "calcite/generic.bak", GenericConfig)
        moveLegacyConfig("calcite/modules/default.json", "calcite/modules/default.bak", ModuleConfig)
        moveLegacyConfig("KAMIBlueCoords.json", "calcite/waypoints.json")
        moveLegacyConfig("KAMIBlueWaypoints.json", "calcite/waypoints.json")
        moveLegacyConfig("KAMIBlueMacros.json", "calcite/macros.json")
        moveLegacyConfig("KAMIBlueFriends.json", "calcite/friends.json")
        moveLegacyConfig("chat_filter.json", "calcite/chat_filter.json")
    }

    private fun moveLegacyConfig(oldConfigIn: String, oldConfigBakIn: String, newConfig: IConfig) {
        if (newConfig.file.exists() || newConfig.backup.exists()) return

        val oldConfig = File(oldConfigIn)
        val oldConfigBak = File(oldConfigBakIn)

        if (!oldConfig.exists() && !oldConfigBak.exists()) return

        try {
            newConfig.file.parentFile.mkdirs()
            Files.move(oldConfig.absoluteFile.toPath(), newConfig.file.absoluteFile.toPath())
        } catch (e: Exception) {
            calcite.client.CalciteMod.LOG.warn("Error moving legacy config", e)
        }

        try {
            newConfig.backup.parentFile.mkdirs()
            Files.move(oldConfigBak.absoluteFile.toPath(), newConfig.backup.absoluteFile.toPath())
        } catch (e: Exception) {
            calcite.client.CalciteMod.LOG.warn("Error moving legacy config", e)
        }
    }

    private fun moveLegacyConfig(oldConfigIn: String, newConfigIn: String) {
        val newConfig = File(newConfigIn)
        if (newConfig.exists()) return

        val oldConfig = File(oldConfigIn)
        if (!oldConfig.exists()) return

        try {
            newConfig.parentFile.mkdirs()
            Files.move(oldConfig.absoluteFile.toPath(), newConfig.absoluteFile.toPath())
        } catch (e: Exception) {
            calcite.client.CalciteMod.LOG.warn("Error moving legacy config", e)
        }
    }

}