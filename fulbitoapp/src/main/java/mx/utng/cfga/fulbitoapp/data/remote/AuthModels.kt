package mx.utng.cfga.fulbitoapp.data.remote

/**
 * Representa a un usuario autenticado en el sistema.
 * 
 * @property id Identificador único de MongoDB.
 * @property username Nombre de usuario (ej. "Ronaldo_7").
 * @property role Rol del usuario ("ADMIN" o "PLAYER").
 */

data class User(
    val id: String,
    val username: String,
    val role: String
)

/**
 * Respuesta del backend tras un login exitoso.
 * Contiene el token JWT (si aplica) y la información del usuario.
 */
data class AuthResponse(
    val message: String,
    val user: User
)

/**
 * Petición enviada al backend para autenticar a un usuario o crearlo.
 */
data class AuthRequest(
    val username: String,
    val password: String,
    val role: String? = null
)

data class GeneratedCredentials(
    val username: String,
    val password: String
)

data class GenerateAccountResponse(
    val message: String,
    val credentials: GeneratedCredentials
)

data class ChangePasswordRequest(
    val userId: String,
    val oldPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val message: String
)

data class PlayerMatchHistory(
    val matchId: String?,
    val teamId: Int,
    val dorsal: String?,
    val goals: Int,
    val yellowCards: Int,
    val redCards: Int
)

data class MyStatsResponse(
    val matchesPlayed: Int,
    val totalGoals: Int,
    val yellowCards: Int,
    val redCards: Int,
    val history: List<PlayerMatchHistory>
)
