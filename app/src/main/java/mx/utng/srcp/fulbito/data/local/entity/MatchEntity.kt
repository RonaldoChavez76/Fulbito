package mx.utng.srcp.fulbito.data.local.entity

import com.google.gson.annotations.SerializedName

data class MatchEntity(
    @SerializedName("_id") val id: String? = null,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val currentPeriod: String = "1ER TIEMPO",
    val elapsedTimeSeconds: Long = 0,
    val isPaused: Boolean = true,
    val isFinished: Boolean = false
)
