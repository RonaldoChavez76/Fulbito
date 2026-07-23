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

interface ApiService {
    @Multipart
    @POST("upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): UploadResponse

    // Teams
    @GET("teams")
    suspend fun getTeams(): List<Team>
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
    suspend fun getMatches(): List<Match>
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
