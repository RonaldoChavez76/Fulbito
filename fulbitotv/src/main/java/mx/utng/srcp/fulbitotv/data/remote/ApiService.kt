package mx.utng.srcp.fulbitotv.data.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/matches")
    suspend fun getMatches(): Response<List<Match>>

    @GET("api/matches/{id}")
    suspend fun getMatchDetails(@Path("id") id: String): Response<MatchDetailsResponse>

    @GET("api/teams")
    suspend fun getTeams(): Response<List<Team>>

    @GET("api/players/top-scorers")
    suspend fun getTopScorers(): Response<List<TopScorer>>
}

object RetrofitInstance {
    const val currentIp = "192.168.1.9" // TODO:cambiarla por la ip del dispositivo real o maquina virtual
    private const val BASE_URL = "http://$currentIp:3000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
