package calcite.capeapi

import com.google.gson.annotations.SerializedName
import java.util.*


data class PlayerProfile(
    @SerializedName("uuid", alternate = ["UUID"])
    val uuid: UUID,
    @SerializedName("name", alternate = ["Name"])
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        return this === other
                || other is PlayerProfile
                && other.uuid == uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}

