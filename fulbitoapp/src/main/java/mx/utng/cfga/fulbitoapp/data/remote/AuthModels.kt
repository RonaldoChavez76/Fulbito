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
