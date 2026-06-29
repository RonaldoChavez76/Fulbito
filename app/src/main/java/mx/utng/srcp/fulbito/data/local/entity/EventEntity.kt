package mx.utng.srcp.fulbito.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EventType {
    GOAL, YELLOW_CARD, RED_CARD
}

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: EventType,
    val playerDorsal: String,
    val teamId: Int,
    val timestampSeconds: Long,
    val period: String
)
