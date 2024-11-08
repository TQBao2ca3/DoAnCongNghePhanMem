package com.example.demo_cnpm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_cnpm.ui.theme.Demo_CNPMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Demo_CNPMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ClockScreen()
                }
            }
        }
    }
}


@Preview
@Composable
fun ClockScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Đồng hồ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "12:00:00 AM",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Hiện tại: dd/MM/yyyy"
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { /* Thực hiện hành động khi nút tròn được nhấn */ },
            modifier = Modifier.size(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
            )
        ) {}
        Button(
            onClick = { /* Thực hiện hành động khi nút cộng được nhấn */ },
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",

            )
        }
    }
}