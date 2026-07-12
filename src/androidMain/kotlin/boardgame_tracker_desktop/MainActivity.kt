package boardgame_tracker_desktop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import boardgame_tracker_desktop.initial_interface_UI.Screens.FirstPage
import boardgame_tracker_desktop.initial_interface_UI.UITheme.SeaBattleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SeaBattleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    FirstPage(onContinue = {
                        finish()
                    })
                }
            }
        }
    }
}
