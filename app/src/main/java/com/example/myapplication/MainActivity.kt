package com.example.myapplication

import TimekeeperScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


// Define Clock Modes
enum class ClockMode {
    DIGITAL,
    ANALOG
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimekeeperScreen()
        }
    }


}
