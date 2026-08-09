package mx.utng.cfga.fulbitoapp.data.remote

data class League(
    val _id: String? = null,
    val name: String = "",
    val description: String = "",
    val logoUrl: String? = ""
)

data class Team(
    val _id: String? = null,
    val name: String = "",
    val category: String = "Mayor",
    val captain: String = "",
    val shieldUrl: String? = "",
    val leagues: List<String> = emptyList(),
    val captainDorsal: String? = null
)

data class Player(
    val _id: String? = null,
    val name: String? = "",
    val dorsal: String? = "",
    val position: String? = "Jugador",
    val photoUrl: String? = "",
    val teamRef: String? = null,
    val matchId: String? = null,
    val isCaptain: Boolean = false
)

data class Match(
    val _id: String? = null,
    val homeTeam: String? = "",
    val awayTeam: String? = "",
    val homeTeamRef: String? = null,
    val awayTeamRef: String? = null,
    val leagueRef: String? = null,
    val fecha: String? = "",
    val hora: String? = "",
    val cancha: String? = "",
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val currentPeriod: String = "1ER TIEMPO",
    val isFinished: Boolean = false
)

data class UploadResponse(
    val url: String
)

data class CaptainInfoResponse(
    val isCaptain: Boolean,
    val team: Team? = null,
    val players: List<Player> = emptyList(),
    val upcomingMatches: List<Match> = emptyList()
)
