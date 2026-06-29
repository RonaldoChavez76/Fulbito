package mx.utng.srcp.fulbito.data.remote

import mx.utng.srcp.fulbito.data.local.entity.EventEntity
import mx.utng.srcp.fulbito.data.local.entity.MatchEntity
import mx.utng.srcp.fulbito.data.local.entity.PlayerEntity
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("matches")
    suspend fun getAllMatches(): Response<List<MatchEntity>>

    @GET("matches/{id}")
    suspend fun getMatchDetails(@Path("id") id: String): Response<MatchDetailsResponse>

    @POST("matches")
    suspend fun createMatch(@Body match: MatchEntity): Response<MatchEntity>

    @PUT("matches/{id}")
    @JvmSuppressWildcards
    suspend fun updateMatchStatus(@Path("id") id: String, @Body status: Map<String, Any>): Response<MatchEntity>

    @GET("players/match/{matchId}")
    suspend fun getPlayersByMatch(@Path("matchId") matchId: String): Response<List<PlayerEntity>>

    @POST("players/sync")
    suspend fun syncManualPlayer(@Body player: PlayerEntity): Response<PlayerEntity>

    @POST("events")
    suspend fun registerEvent(@Body event: EventEntity): Response<EventEntity>

    @PUT("events/{id}")
    @JvmSuppressWildcards
    suspend fun updateEvent(@Path("id") id: String, @Body data: Map<String, Any>): Response<EventEntity>

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): Response<Void>
}

data class MatchDetailsResponse(
    val partido: MatchEntity,
    val jugadores: List<PlayerEntity>,
    val eventos: List<EventEntity>
)
