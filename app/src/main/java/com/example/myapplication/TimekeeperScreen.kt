import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Timer
import com.example.myapplication.ClockScreen

@Composable
fun TimekeeperScreen() {
    var currentScreen by remember { mutableStateOf("Clock") }

    Scaffold(
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = currentScreen == "Clock",
                    onClick = { currentScreen = "Clock" },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Clock"
                        )
                    },
                    label = { Text("Clock") }
                )
                BottomNavigationItem(
                    selected = currentScreen == "Stopwatch",
                    onClick = { currentScreen = "Stopwatch" },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Stopwatch"
                        )
                    },
                    label = { Text("Stopwatch") }
                )
            }
        }
    ) { innerPadding ->
        // Sử dụng innerPadding cho nội dung
        when (currentScreen) {
            "Clock" -> ClockScreen(
                isDarkTheme = false,
                onToggleTheme = { /* Logic toggle theme */ },
                modifier = Modifier.padding(innerPadding) // Thêm padding
            )
            "Stopwatch" -> StopwatchScreen(
                modifier = Modifier.padding(innerPadding) // Thêm padding
            )
        }
    }
}
