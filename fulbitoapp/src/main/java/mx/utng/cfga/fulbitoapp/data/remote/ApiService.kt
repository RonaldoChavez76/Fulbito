package mx.utng.cfga.fulbitoapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.*
import okhttp3.MultipartBody

/**
 * Interfaz ApiService
 *
 * Define todos los endpoints (rutas) de la API REST del backend Node.js.
 * Utiliza Retrofit para convertir las funciones suspendidas de Kotlin 
 * en peticiones HTTP (GET, POST, PUT, DELETE) de manera asíncrona.
 */
interface ApiService {
    @Multipart
    @POST("upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): UploadResponse

    // Leagues
    @GET("leagues")
    suspend fun getLeagues(): List<League>
    @GET("leagues/{id}")
    suspend fun getLeagueById(@Path("id") id: String): League
    @POST("leagues")
    suspend fun createLeague(@Body league: League): League
    @PUT("leagues/{id}")
    suspend fun updateLeague(@Path("id") id: String, @Body league: League): League
    @DELETE("leagues/{id}")
    suspend fun deleteLeague(@Path("id") id: String)

    // Teams
    @GET("teams")
    suspend fun getTeams(@Query("leagueId") leagueId: String? = null): List<Team>
    @POST("teams")
    suspend fun createTeam(@Body team: Team): Team
    @PUT("teams/{id}")
    suspend fun updateTeam(@Path("id") id: String, @Body team: Team): Team
    @DELETE("teams/{id}")
    suspend fun deleteTeam(@Path("id") id: String)

    // Players
    @GET("players")
    suspend fun getPlayers(): List<Player>
    @POST("players")
    suspend fun createPlayer(@Body player: Player): Player
    @PUT("players/{id}")
    suspend fun updatePlayer(@Path("id") id: String, @Body player: Player): Player
    @DELETE("players/{id}")
    suspend fun deletePlayer(@Path("id") id: String)

    // Matches
    @GET("matches")
    suspend fun getMatches(@Query("leagueId") leagueId: String? = null): List<Match>
    @POST("matches")
    suspend fun createMatch(@Body match: Match): Match
    @PUT("matches/{id}")
    suspend fun updateMatch(@Path("id") id: String, @Body match: Match): Match
    @DELETE("matches/{id}")
    suspend fun deleteMatch(@Path("id") id: String)
    
    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse
    
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse
    
    @PUT("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ChangePasswordResponse

    @POST("players/{id}/generate-account")
    suspend fun generateAccount(@Path("id") id: String): GenerateAccountResponse
    
    @GET("players/my-stats/{userId}")
    suspend fun getMyStats(@Path("userId") userId: String): MyStatsResponse

    @GET("players/captain-info/{userId}")
    suspend fun getCaptainInfo(@Path("userId") userId: String): CaptainInfoResponse
}

object RetrofitInstance {
    private var _api: ApiService? = null

    fun getApi(): ApiService {
        if (_api == null) {
            _api = Retrofit.Builder()
                // Se agrega el slash al final de BASE_URL si no lo tiene, Retrofit lo exige
                .baseUrl(if (ApiConfig.BASE_URL.endsWith("/")) ApiConfig.BASE_URL else "${ApiConfig.BASE_URL}/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
        return _api!!
    }
}
