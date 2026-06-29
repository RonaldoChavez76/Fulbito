package mx.utng.srcp.fulbito.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.utng.srcp.fulbito.data.local.entity.*
import mx.utng.srcp.fulbito.data.remote.RetrofitClient

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.apiService

    private val _match = MutableStateFlow<MatchEntity?>(null)
    val match: StateFlow<MatchEntity?> = _match.asStateFlow()

    private val _players = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val players: StateFlow<List<PlayerEntity>> = _players.asStateFlow()

    private val _events = MutableStateFlow<List<EventEntity>>(emptyList())
    val events: StateFlow<List<EventEntity>> = _events.asStateFlow()

    private var timerJob: Job? = null
    private var currentMatchId: String? = null

    init {
        loadOrInitializeMatch()
        
        viewModelScope.launch {
            match.collect { m ->
                if (m != null && !m.isPaused && !m.isFinished && timerJob == null) {
                    startTimer()
                } else if (m != null && (m.isPaused || m.isFinished) && timerJob != null) {
                    stopTimer()
                }
            }
        }
    }

    private fun loadOrInitializeMatch() {
        viewModelScope.launch {
            try {
                val response = api.getAllMatches()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val latestMatch = response.body()!!.first()
                    currentMatchId = latestMatch.id
                    fetchFullMatchDetails(currentMatchId!!)
                } else {
                    createNewMatch()
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading match", e)
            }
        }
    }

    private suspend fun createNewMatch() {
        try {
            val newMatch = MatchEntity(homeTeam = "Toros FC", awayTeam = "Máquinas")
            val response = api.createMatch(newMatch)
            if (response.isSuccessful) {
                val createdMatch = response.body()!!
                currentMatchId = createdMatch.id
                _match.value = createdMatch
                
                // Seed initial players for the new match
                seedPlayers(createdMatch.id!!)
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error creating match", e)
        }
    }

    private suspend fun seedPlayers(matchId: String) {
        val initialPlayers = listOf(
            PlayerEntity(matchId = matchId, dorsal = "7", name = "Juan", teamId = 0),
            PlayerEntity(matchId = matchId, dorsal = "10", name = "Pedro", teamId = 0),
            PlayerEntity(matchId = matchId, dorsal = "14", name = "Luis", teamId = 0),
            PlayerEntity(matchId = matchId, dorsal = "9", name = "Gomez", teamId = 1),
            PlayerEntity(matchId = matchId, dorsal = "3", name = "Silva", teamId = 1),
            PlayerEntity(matchId = matchId, dorsal = "11", name = "Rojas", teamId = 1),
            PlayerEntity(matchId = matchId, dorsal = "6", name = "Diaz", teamId = 0),
            PlayerEntity(matchId = matchId, dorsal = "2", name = "Perez", teamId = 1)
        )
        initialPlayers.forEach { api.syncManualPlayer(it) }
        fetchFullMatchDetails(matchId)
    }

    private fun fetchFullMatchDetails(matchId: String) {
        viewModelScope.launch {
            try {
                val response = api.getMatchDetails(matchId)
                if (response.isSuccessful) {
                    val details = response.body()!!
                    _match.value = details.partido
                    _players.value = details.jugadores
                    _events.value = details.eventos.sortedByDescending { it.timestampSeconds }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching details", e)
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _match.value?.let { m ->
                    val newTime = m.elapsedTimeSeconds + 1
                    _match.value = m.copy(elapsedTimeSeconds = newTime)
                    // Sync time with backend occasionally or on specific actions
                    if (newTime % 30 == 0L) { // Every 30 seconds
                         syncStatusWithBackend()
                    }
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        syncStatusWithBackend()
    }

    private fun syncStatusWithBackend() {
        viewModelScope.launch {
            val m = _match.value ?: return@launch
            try {
                api.updateMatchStatus(m.id!!, mapOf(
                    "elapsedTimeSeconds" to m.elapsedTimeSeconds,
                    "isPaused" to m.isPaused,
                    "isFinished" to m.isFinished,
                    "currentPeriod" to m.currentPeriod
                ))
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error syncing status", e)
            }
        }
    }

    fun togglePause() {
        _match.value?.let { m ->
            _match.value = m.copy(isPaused = !m.isPaused)
            syncStatusWithBackend()
        }
    }

    fun finishMatch() {
        _match.value?.let { m ->
            _match.value = m.copy(isPaused = true, isFinished = true, currentPeriod = "FIN")
            syncStatusWithBackend()
        }
    }

    fun reopenMatch() {
        _match.value?.let { m ->
            _match.value = m.copy(isPaused = true, isFinished = false, currentPeriod = "2DO TIEMPO")
            syncStatusWithBackend()
        }
    }

    fun nextPeriod() {
        _match.value?.let { m ->
            val nextPeriod = if (m.currentPeriod == "1ER TIEMPO") "2DO TIEMPO" else "FIN"
            val newElapsedTime = if (m.currentPeriod == "1ER TIEMPO") 0L else m.elapsedTimeSeconds
            _match.value = m.copy(
                currentPeriod = nextPeriod,
                isPaused = true,
                elapsedTimeSeconds = newElapsedTime,
                isFinished = nextPeriod == "FIN"
            )
            syncStatusWithBackend()
        }
    }

    fun registerEvent(type: EventType, dorsal: String, teamId: Int) {
        val m = _match.value ?: return
        viewModelScope.launch {
            try {
                // Ensure player exists in backend
                api.syncManualPlayer(PlayerEntity(matchId = m.id, dorsal = dorsal, teamId = teamId))
                
                // CRITICAL: Sync current match status (time/period) first to avoid desync on refresh
                api.updateMatchStatus(m.id!!, mapOf(
                    "elapsedTimeSeconds" to m.elapsedTimeSeconds,
                    "isPaused" to m.isPaused,
                    "isFinished" to m.isFinished,
                    "currentPeriod" to m.currentPeriod
                ))

                val newEvent = EventEntity(
                    matchId = m.id,
                    type = type,
                    playerDorsal = dorsal,
                    teamId = teamId,
                    timestampSeconds = m.elapsedTimeSeconds,
                    period = m.currentPeriod
                )
                val response = api.registerEvent(newEvent)
                if (response.isSuccessful) {
                    fetchFullMatchDetails(m.id!!) // Now it will fetch the updated status
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error registering event", e)
            }
        }
    }

    // Refactored updateEvent to use String ID
    fun updateEventById(eventId: String, newDorsal: String) {
        viewModelScope.launch {
            try {
                val response = api.updateEvent(eventId, mapOf("playerDorsal" to newDorsal))
                if (response.isSuccessful) {
                    fetchFullMatchDetails(currentMatchId!!)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error updating event", e)
            }
        }
    }

    fun deleteEventById(eventId: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteEvent(eventId)
                if (response.isSuccessful) {
                    fetchFullMatchDetails(currentMatchId!!)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error deleting event", e)
            }
        }
    }
}
