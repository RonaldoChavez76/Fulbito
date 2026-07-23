package mx.utng.cfga.fulbitoapp.presentation

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.utng.cfga.fulbitoapp.data.remote.ApiConfig
import mx.utng.cfga.fulbitoapp.data.remote.Match
import mx.utng.cfga.fulbitoapp.data.remote.Player
import mx.utng.cfga.fulbitoapp.data.remote.RetrofitInstance
import mx.utng.cfga.fulbitoapp.data.remote.Team
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class AdminViewModel : ViewModel() {
    private val api get() = RetrofitInstance.getApi()

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    init {
        fetchTeams()
        fetchPlayers()
        fetchMatches()
    }

    fun reloadAll() {
        fetchTeams()
        fetchPlayers()
        fetchMatches()
    }

    // --- Teams ---
    fun fetchTeams() {
        viewModelScope.launch {
            try {
                _teams.value = api.getTeams()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun createTeam(team: Team) {
        viewModelScope.launch {
            try {
                api.createTeam(team)
                fetchTeams()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun updateTeam(id: String, team: Team) {
        viewModelScope.launch {
            try {
                api.updateTeam(id, team)
                fetchTeams()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun deleteTeam(id: String) {
        viewModelScope.launch {
            try {
                api.deleteTeam(id)
                fetchTeams()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // --- Players ---
    fun fetchPlayers() {
        viewModelScope.launch {
            try {
                _players.value = api.getPlayers()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun createPlayer(player: Player) {
        viewModelScope.launch {
            try {
                api.createPlayer(player)
                fetchPlayers()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun updatePlayer(id: String, player: Player) {
        viewModelScope.launch {
            try {
                api.updatePlayer(id, player)
                fetchPlayers()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun deletePlayer(id: String) {
        viewModelScope.launch {
            try {
                api.deletePlayer(id)
                fetchPlayers()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // --- Matches ---
    fun fetchMatches() {
        viewModelScope.launch {
            try {
                _matches.value = api.getMatches()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    suspend fun uploadImage(context: Context, uri: Uri): String? {
        return try {
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "upload_image_${System.currentTimeMillis()}.$extension")
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
            
            val response = api.uploadImage(body)
            // Build the full image URL using the base host from ApiConfig
            val baseHost = ApiConfig.BASE_URL
                .removePrefix("http://")
                .removePrefix("https://")
                .substringBefore("/api")
            "http://$baseHost${response.url}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createMatch(match: Match) {
        viewModelScope.launch {
            try {
                api.createMatch(match)
                fetchMatches()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun updateMatch(id: String, match: Match) {
        viewModelScope.launch {
            try {
                api.updateMatch(id, match)
                fetchMatches()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun deleteMatch(id: String) {
        viewModelScope.launch {
            try {
                api.deleteMatch(id)
                fetchMatches()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}
