package calcite.client.manager.managers

import calcite.capeapi.AbstractUUIDManager
import calcite.capeapi.PlayerProfile
import calcite.capeapi.UUIDUtils
import calcite.client.CalciteMod
import calcite.client.manager.Manager
import calcite.client.util.Wrapper

object UUIDManager : AbstractUUIDManager(calcite.client.CalciteMod.DIRECTORY + "uuid_cache.json", calcite.client.CalciteMod.LOG, maxCacheSize = 1000), Manager {

    override fun getOrRequest(nameOrUUID: String): PlayerProfile? {
        return Wrapper.minecraft.connection?.playerInfoMap?.let { playerInfoMap ->
            val infoMap = ArrayList(playerInfoMap)
            val isUUID = UUIDUtils.isUUID(nameOrUUID)
            val withOutDashes = UUIDUtils.removeDashes(nameOrUUID)

            infoMap.find {
                isUUID && UUIDUtils.removeDashes(it.gameProfile.id.toString()).equals(withOutDashes, ignoreCase = true)
                    || !isUUID && it.gameProfile.name.equals(nameOrUUID, ignoreCase = true)
            }?.gameProfile?.let {
                PlayerProfile(it.id, it.name)
            }
        } ?: super.getOrRequest(nameOrUUID)
    }
}
