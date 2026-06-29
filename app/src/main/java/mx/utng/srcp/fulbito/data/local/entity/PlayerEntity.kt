package mx.utng.srcp.fulbito.data.local.entity

import com.google.gson.annotations.SerializedName

data class PlayerEntity(
    @SerializedName("_id") val id: String? = null,
    val matchId: String? = null,
    val dorsal: String,
    val name: String = "",
    val teamId: Int, // 0 for home, 1 for away
    val isManualEntry: Boolean = false
)
