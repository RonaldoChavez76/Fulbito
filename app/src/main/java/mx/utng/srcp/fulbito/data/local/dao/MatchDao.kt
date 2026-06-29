package mx.utng.srcp.fulbito.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.utng.srcp.fulbito.data.local.entity.EventEntity
import mx.utng.srcp.fulbito.data.local.entity.MatchEntity
import mx.utng.srcp.fulbito.data.local.entity.PlayerEntity

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE id = 1")
    fun getMatch(): Flow<MatchEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("SELECT * FROM players")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Query("SELECT * FROM events ORDER BY timestampSeconds DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert
    suspend fun insertEvent(event: EventEntity)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY id DESC LIMIT 1")
    suspend fun getLastEvent(): EventEntity?
}
