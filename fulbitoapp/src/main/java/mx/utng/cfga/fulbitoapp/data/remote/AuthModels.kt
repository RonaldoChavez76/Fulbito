package mx.utng.cfga.fulbitoapp.data.remote

data class User(
    val id: String,
    val username: String,
    val role: String
)

data class AuthResponse(
    val message: String,
    val user: User
)

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
