package mx.utng.srcp.fulbito.data.local.entity

import com.google.gson.annotations.SerializedName

enum class EventType {
    GOAL, YELLOW_CARD, RED_CARD
}

data class EventEntity(
    @SerializedName("_id") val id: String? = null,
    val matchId: String? = null,
    val type: EventType,
    val playerDorsal: String,
    val teamId: Int,
    val timestampSeconds: Long,
    val period: String
)
