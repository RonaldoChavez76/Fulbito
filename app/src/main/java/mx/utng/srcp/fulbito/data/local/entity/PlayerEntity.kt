package mx.utng.srcp.fulbito.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dorsal: String,
    val name: String,
    val teamId: Int // 0 for home, 1 for away
)
