package mx.utng.cfga.fulbitoapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.utng.cfga.fulbitoapp.data.remote.AuthRequest
import mx.utng.cfga.fulbitoapp.data.remote.RetrofitInstance
import mx.utng.cfga.fulbitoapp.data.remote.User

class LoginViewModel : ViewModel() {
    private val api = RetrofitInstance.getApi()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun login(username: String, password: String, onLoginSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.login(AuthRequest(username, password))
                _user.value = response.user
                if (response.user.role == "Admin" || response.user.role == "Jugador") {
                    onLoginSuccess(response.user.role)
                } else {
                    _error.value = "No tienes permisos para acceder a esta aplicación."
                }
            } catch (e: Exception) {
                _error.value = "Credenciales incorrectas o error de red."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun logout() {
        _user.value = null
    }
}
