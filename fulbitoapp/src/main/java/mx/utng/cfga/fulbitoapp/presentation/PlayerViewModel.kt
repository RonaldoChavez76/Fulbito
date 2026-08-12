package mx.utng.cfga.fulbitoapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.utng.cfga.fulbitoapp.data.remote.CaptainInfoResponse
import mx.utng.cfga.fulbitoapp.data.remote.ChangePasswordRequest
import mx.utng.cfga.fulbitoapp.data.remote.MyStatsResponse
import mx.utng.cfga.fulbitoapp.data.remote.RetrofitInstance

/**
 * PlayerViewModel
 *
 * Se encarga de gestionar el estado y la lógica de negocio para la vista del jugador.
 * Conecta la UI (Jetpack Compose) con la API REST (Retrofit).
 */
class PlayerViewModel : ViewModel() {
    private val api = RetrofitInstance.getApi()

    private val _stats = MutableStateFlow<MyStatsResponse?>(null)
    val stats: StateFlow<MyStatsResponse?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _captainInfo = MutableStateFlow<CaptainInfoResponse?>(null)
    val captainInfo: StateFlow<CaptainInfoResponse?> = _captainInfo.asStateFlow()

    /**
     * Obtiene las estadísticas globales del jugador (Goles, Partidos, Tarjetas).
     * Se comunica con la ruta GET /api/players/:userId/stats del backend.
     * 
     * @param userId El ID único de MongoDB del jugador logueado.
     */
    fun fetchMyStats(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _stats.value = api.getMyStats(userId)
            } catch (e: Exception) {
                _error.value = "Error al obtener estadísticas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cambia la contraseña del jugador mediante la API segura.
     *
     * @param userId ID del jugador.
     * @param oldPass Contraseña actual (para validación de seguridad).
     * @param newPass La nueva contraseña deseada.
     * @param onSuccess Callback que se ejecuta si el cambio es exitoso.
     * @param onError Callback que devuelve un mensaje de error si falla la validación.
     */
    fun changePassword(userId: String, oldPass: String, newPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val req = ChangePasswordRequest(userId, oldPass, newPass)
                api.changePassword(req)
                onSuccess()
            } catch (e: Exception) {
                onError("Error al cambiar contraseña, verifica tu contraseña actual.")
            }
        }
    }

    /**
     * Consulta si el jugador logueado tiene el rol de "Capitán".
     * Si es capitán, el backend también devuelve la lista de jugadores de su equipo
     * y los próximos partidos programados para mostrarlos en el Dashboard.
     *
     * @param userId ID del jugador logueado.
     */
    fun fetchCaptainInfo(userId: String) {
        viewModelScope.launch {
            try {
                _captainInfo.value = api.getCaptainInfo(userId)
            } catch (e: Exception) {
                _captainInfo.value = CaptainInfoResponse(isCaptain = false)
            }
        }
    }
}
