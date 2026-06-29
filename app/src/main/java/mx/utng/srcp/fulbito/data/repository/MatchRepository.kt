package mx.utng.srcp.fulbito.data.repository

import kotlinx.coroutines.flow.Flow
import mx.utng.srcp.fulbito.data.local.dao.MatchDao
import mx.utng.srcp.fulbito.data.local.entity.EventEntity
import mx.utng.srcp.fulbito.data.local.entity.MatchEntity
import mx.utng.srcp.fulbito.data.local.entity.PlayerEntity

class MatchRepository(private val matchDao: MatchDao) {
    val match: Flow<MatchEntity?> = matchDao.getMatch()
    val allPlayers: Flow<List<PlayerEntity>> = matchDao.getAllPlayers()
    val allEvents: Flow<List<EventEntity>> = matchDao.getAllEvents()

    suspend fun insertMatch(match: MatchEntity) = matchDao.insertMatch(match)
    suspend fun updateMatch(match: MatchEntity) = matchDao.updateMatch(match)
    suspend fun insertPlayers(players: List<PlayerEntity>) = matchDao.insertPlayers(players)
    suspend fun insertEvent(event: EventEntity) = matchDao.insertEvent(event)
    suspend fun updateEvent(event: EventEntity) = matchDao.updateEvent(event)
    suspend fun deleteEvent(event: EventEntity) = matchDao.deleteEvent(event)
    suspend fun getLastEvent() = matchDao.getLastEvent()
}
