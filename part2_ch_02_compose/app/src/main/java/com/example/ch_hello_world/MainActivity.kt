package com.example.ch_hello_world

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch_hello_world.ui.theme.Ch_hello_worldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ch_hello_worldTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )

                    MyScreen()
                }
            }
        }
    }
}

@Composable
fun MyScreen() {
    var isVisible by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("Hello") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text)
        Button(onClick = {
            text = "Clicked!"
            isVisible = !isVisible
        }) {
            Text("Click to fade out")
        }

        val alpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f)
        Box(modifier = Modifier.alpha(alpha)) {
            Text("Fade string")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    Ch_hello_worldTheme {
        MyScreen()
    }
}

