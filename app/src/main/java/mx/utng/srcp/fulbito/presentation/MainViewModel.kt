package mx.utng.srcp.fulbito.presentation

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.utng.srcp.fulbito.data.local.entity.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _match = MutableStateFlow<MatchEntity?>(null)
    val match: StateFlow<MatchEntity?> = _match.asStateFlow()

    private val _players = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val players: StateFlow<List<PlayerEntity>> = _players.asStateFlow()

    private val _events = MutableStateFlow<List<EventEntity>>(emptyList())
    val events: StateFlow<List<EventEntity>> = _events.asStateFlow()

    private var timerJob: Job? = null

    init {
        seedDatabase()
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

    private fun seedDatabase() {
        _match.value = MatchEntity(
            homeTeam = "Toros FC",
            awayTeam = "Máquinas",
            isPaused = true
        )

        _players.value = listOf(
            PlayerEntity(id = 1, dorsal = "7", name = "Juan", teamId = 0),
            PlayerEntity(id = 2, dorsal = "10", name = "Pedro", teamId = 0),
            PlayerEntity(id = 3, dorsal = "14", name = "Luis", teamId = 0),
            PlayerEntity(id = 4, dorsal = "9", name = "Gomez", teamId = 1),
            PlayerEntity(id = 5, dorsal = "3", name = "Silva", teamId = 1),
            PlayerEntity(id = 6, dorsal = "11", name = "Rojas", teamId = 1),
            PlayerEntity(id = 7, dorsal = "6", name = "Diaz", teamId = 0),
            PlayerEntity(id = 8, dorsal = "2", name = "Perez", teamId = 1)
        )
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _match.value?.let { m ->
                    _match.value = m.copy(elapsedTimeSeconds = m.elapsedTimeSeconds + 1)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun togglePause() {
        _match.value?.let { m ->
            _match.value = m.copy(isPaused = !m.isPaused)
        }
    }

    fun finishMatch() {
        _match.value?.let { m ->
            _match.value = m.copy(isPaused = true, isFinished = true)
        }
    }

    fun nextPeriod() {
        _match.value?.let { m ->
            val nextPeriod = if (m.currentPeriod == "1ER TIEMPO") "2DO TIEMPO" else "FIN"
            // Reset timer only if transitioning from 1st to 2nd half
            val newElapsedTime = if (m.currentPeriod == "1ER TIEMPO") 0L else m.elapsedTimeSeconds
            _match.value = m.copy(
                currentPeriod = nextPeriod,
                isPaused = true,
                elapsedTimeSeconds = newElapsedTime,
                isFinished = nextPeriod == "FIN"
            )
        }
    }

    fun registerEvent(type: EventType, dorsal: String, teamId: Int) {
        _match.value?.let { m ->
            // Si el dorsal no existe en la lista actual, agregarlo como jugador temporal
            val playerExists = _players.value.any { it.dorsal == dorsal && it.teamId == teamId }
            if (!playerExists) {
                val newPlayer = PlayerEntity(
                    id = (_players.value.size + 1).toLong(),
                    dorsal = dorsal,
                    name = "Jugador $dorsal",
                    teamId = teamId
                )
                _players.value = _players.value + newPlayer
            }

            val event = EventEntity(
                id = (_events.value.size + 1).toLong(),
                type = type,
                playerDorsal = dorsal,
                teamId = teamId,
                timestampSeconds = m.elapsedTimeSeconds,
                period = m.currentPeriod
            )
            _events.value = listOf(event) + _events.value

            if (type == EventType.GOAL) {
                if (teamId == 0) {
                    _match.value = m.copy(homeScore = m.homeScore + 1)
                } else {
                    _match.value = m.copy(awayScore = m.awayScore + 1)
                }
            }
        }
    }

    fun updateEvent(eventId: Long, newDorsal: String) {
        val currentEvents = _events.value.toMutableList()
        val index = currentEvents.indexOfFirst { it.id == eventId }
        if (index != -1) {
            currentEvents[index] = currentEvents[index].copy(playerDorsal = newDorsal)
            _events.value = currentEvents
        }
    }

    fun deleteEvent(eventId: Long) {
        val currentEvents = _events.value.toMutableList()
        val eventToDelete = currentEvents.find { it.id == eventId }
        if (eventToDelete != null) {
            currentEvents.remove(eventToDelete)
            _events.value = currentEvents

            _match.value?.let { m ->
               if (eventToDelete.type == EventType.GOAL) {
                   if (eventToDelete.teamId == 0) {
                       _match.value = m.copy(homeScore = (m.homeScore - 1).coerceAtLeast(0))
                   } else {
                       _match.value = m.copy(awayScore = (m.awayScore - 1).coerceAtLeast(0))
                   }
               }
            }
        }
    }
}
