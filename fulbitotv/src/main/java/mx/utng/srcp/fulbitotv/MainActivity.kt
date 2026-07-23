package mx.utng.srcp.fulbitotv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Surface
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.utng.srcp.fulbitotv.presentation.TvViewModel
import mx.utng.srcp.fulbitotv.presentation.screens.TvLiveScoreScreen
import mx.utng.srcp.fulbitotv.presentation.screens.TvWaitingScreen
import mx.utng.srcp.fulbitotv.presentation.screens.BgDark

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: TvViewModel = viewModel()
            
            val activeMatch by viewModel.activeMatch.collectAsState()
            val isLiveMode by viewModel.isLiveMode.collectAsState()
            val matches by viewModel.matches.collectAsState()
            val events by viewModel.matchEvents.collectAsState()
            val players by viewModel.matchPlayers.collectAsState()
            val teams by viewModel.teams.collectAsState()
            val topScorers by viewModel.topScorers.collectAsState()

            Surface(modifier = Modifier.fillMaxSize()) {
                Crossfade(targetState = isLiveMode, label = "ScreenTransition") { liveMode ->
                    if (liveMode && activeMatch != null) {
                        TvLiveScoreScreen(
                            match = activeMatch!!,
                            events = events,
                            players = players,
                            teams = teams,
                            onBack = { viewModel.exitLiveMode() }
                        )
                    } else {
                        TvWaitingScreen(
                            matches = matches, 
                            topScorers = topScorers,
                            onMatchClick = { matchId ->
                                viewModel.selectMatch(matchId)
                            }
                        )
                    }
                }
            }
        }
    }
}