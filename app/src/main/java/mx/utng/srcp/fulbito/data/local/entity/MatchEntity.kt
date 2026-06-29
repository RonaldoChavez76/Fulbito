package mx.utng.srcp.fulbito.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: Int = 1,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val currentPeriod: String = "1ER TIEMPO",
    val elapsedTimeSeconds: Long = 0,
    val isPaused: Boolean = true,
    val isFinished: Boolean = false
)
