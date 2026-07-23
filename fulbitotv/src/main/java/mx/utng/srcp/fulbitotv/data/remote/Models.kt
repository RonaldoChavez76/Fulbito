package mx.utng.srcp.fulbitotv.data.remote

data class Match(
    val _id: String? = null,
    val homeTeam: String? = "",
    val awayTeam: String? = "",
    val homeTeamRef: String? = null,
    val awayTeamRef: String? = null,
    val fecha: String? = "",
    val hora: String? = "",
    val cancha: String? = "",
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val currentPeriod: String = "1ER TIEMPO",
    val elapsedTimeSeconds: Long = 0,
    val isPaused: Boolean = true,
    val isFinished: Boolean = false
)

data class Team(
    val _id: String? = null,
    val name: String = "",
    val shieldUrl: String? = ""
)

data class Event(
    val _id: String? = null,
    val matchId: String? = null,
    val type: String = "GOAL",
    val playerDorsal: String = "",
    val teamId: Int = 0,
    val timestampSeconds: Long = 0,
    val period: String = "1ER TIEMPO"
)

data class TopScorer(
    val name: String,
    val team: String,
    val goals: Int
)

data class Player(
    val _id: String? = null,
    val name: String? = "",
    val dorsal: String? = "",
    val teamId: Int? = null,
    val teamRef: String? = null
)

data class MatchDetailsResponse(
    val partido: Match,
    val jugadores: List<Player> = emptyList(),
    val eventos: List<Event>
)
