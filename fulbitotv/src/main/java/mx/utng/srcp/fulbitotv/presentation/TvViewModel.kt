package mx.utng.srcp.fulbitotv.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.utng.srcp.fulbitotv.data.remote.Event
import mx.utng.srcp.fulbitotv.data.remote.Match
import mx.utng.srcp.fulbitotv.data.remote.RetrofitInstance
import mx.utng.srcp.fulbitotv.data.remote.Team
import mx.utng.srcp.fulbitotv.data.remote.TopScorer
import java.net.URISyntaxException

class TvViewModel : ViewModel() {
    private val api get() = RetrofitInstance.api

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    private val _topScorers = MutableStateFlow<List<TopScorer>>(emptyList())
    val topScorers: StateFlow<List<TopScorer>> = _topScorers
    
    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    private val _activeMatch = MutableStateFlow<Match?>(null)
    val activeMatch: StateFlow<Match?> = _activeMatch

    private val _matchEvents = MutableStateFlow<List<Event>>(emptyList())
    val matchEvents: StateFlow<List<Event>> = _matchEvents

    private val _matchPlayers = MutableStateFlow<List<mx.utng.srcp.fulbitotv.data.remote.Player>>(emptyList())
    val matchPlayers: StateFlow<List<mx.utng.srcp.fulbitotv.data.remote.Player>> = _matchPlayers

    // Controla si la TV muestra el marcador (true) o la sala de espera (false)
    private val _isLiveMode = MutableStateFlow(false)
    val isLiveMode: StateFlow<Boolean> = _isLiveMode

    private var socket: Socket? = null

    init {
        fetchTeams()
        fetchInitialData()
        setupSocket()
    }

    private fun fetchTeams() {
        viewModelScope.launch {
            try {
                val res = api.getTeams()
                if (res.isSuccessful) res.body()?.let { _teams.value = it }
            } catch (e: Exception) { Log.e("TvViewModel", "Error teams", e) }
        }
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            try {
                val scorersRes = api.getTopScorers()
                if (scorersRes.isSuccessful) {
                    _topScorers.value = scorersRes.body() ?: emptyList()
                }

                val matchesRes = api.getMatches()
                if (matchesRes.isSuccessful) {
                    val list = matchesRes.body() ?: emptyList()
                    _matches.value = list
                    // NO auto-navigamos al marcador. El usuario elige.
                }
            } catch (e: Exception) {
                Log.e("TvViewModel", "Error fetching data", e)
            }
        }
    }

    fun selectMatch(matchId: String) {
        viewModelScope.launch {
            try {
                val res = api.getMatchDetails(matchId)
                if (res.isSuccessful && res.body() != null) {
                    val body = res.body()!!
                    _activeMatch.value = body.partido
                    _matchEvents.value = body.eventos
                    _matchPlayers.value = body.jugadores
                    _isLiveMode.value = true // Navegar al marcador
                }
            } catch (e: Exception) {
                Log.e("TvViewModel", "Error selecting match", e)
            }
        }
    }

    fun exitLiveMode() {
        _isLiveMode.value = false
        _activeMatch.value = null
        _matchEvents.value = emptyList()
        _matchPlayers.value = emptyList()
    }

    private fun setupSocket() {
        try {
            socket = IO.socket("http://${RetrofitInstance.currentIp}:3000")
            socket?.connect()

            socket?.on(Socket.EVENT_CONNECT) {
                Log.d("TvViewModel", "Socket.io Connected")
            }

            socket?.on("match_updated") { args ->
                viewModelScope.launch(Dispatchers.Main) {
                    Log.d("TvViewModel", "Match updated event received")
                    val currentId = _activeMatch.value?._id
                    if (currentId != null && _isLiveMode.value) {
                        // Refrescar detalles del partido activo sin perder el modo live
                        try {
                            val res = api.getMatchDetails(currentId)
                            if (res.isSuccessful && res.body() != null) {
                                val body = res.body()!!
                                _activeMatch.value = body.partido
                                _matchEvents.value = body.eventos
                                _matchPlayers.value = body.jugadores
                            }
                        } catch (e: Exception) { Log.e("TvViewModel", "Error refreshing match", e) }
                    }
                    // Siempre actualizar lista de partidos y goleadores
                    try {
                        val matchesRes = api.getMatches()
                        if (matchesRes.isSuccessful) _matches.value = matchesRes.body() ?: emptyList()

                        val scorersRes = api.getTopScorers()
                        if (scorersRes.isSuccessful) _topScorers.value = scorersRes.body() ?: emptyList()
                    } catch (e: Exception) { Log.e("TvViewModel", "Error fetching data on socket update", e) }
                }
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        socket?.disconnect()
    }
}
