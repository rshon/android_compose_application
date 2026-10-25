package com.example.ch_text_and_button

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ch_text_and_button.ui.theme.ChapterTextAndButtonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChapterTextAndButtonTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyScreen()
                }
            }
        }
    }
}

private object RedBackgroundModifier : DrawModifier {
    override fun ContentDrawScope.draw() {
        drawRect(color = Color.Red)
        drawContent()
    }
}

fun Modifier.drawRedBackground(): Modifier =
    this.then(RedBackgroundModifier)

interface MyModifier {
    companion object : MyModifier {
        override fun toString(): String = "Modifier"
    }
}

@Composable
fun MyScreen() {
    var clickCount by remember { mutableStateOf(0) }

    Surface(
        color = Color.LightGray,           // background color
        modifier = Modifier.fillMaxSize()  // fill whole screen
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val myMod: MyModifier = MyModifier

            if (myMod == MyModifier) {
                Text(text = "same!")
            }

            var interactionWord by remember { mutableStateOf("Ping") }
            val commonModifier = Modifier
                .padding(8.dp)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RectangleShape
                )
                .drawRedBackground()

            Text(interactionWord, modifier = commonModifier)
            Button(
                onClick = { interactionWord = "Pong" },
                modifier = commonModifier
            ) {
                Text(
                    text = "interact!",
                    modifier = commonModifier
                )
            }

            Text(
                text = "click count = $clickCount",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { clickCount += 1 },
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp)
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray)
                    .border(2.dp, Color.Red),
                enabled = true,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color.Red),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,           // background color
                    contentColor = Color.White,             // text color
                    disabledContainerColor = Color.Gray,    // background color while disabled
                    disabledContentColor = Color.LightGray  // text color while disabled
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Click Me",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { clickCount += 1 },
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,           // 배경색
                    contentColor = MaterialTheme.colorScheme.onBackground            // 텍스트/아이콘 색
                )
            ) {
                Text(
                    text = "Click Me",
                    color = MaterialTheme.colorScheme.onBackground
                )

//                Text(text = "Click Me",
//                    color = Color.White,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    fontStyle = FontStyle.Italic,
//                    textAlign = TextAlign.Center
//                )

//                Text(
//                    text = "Click Me",
//                    style = TextStyle(
//                        color = Color.White,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        fontStyle = FontStyle.Italic,
//                        textAlign = TextAlign.Center
//                    )
//                )
//
//                Text(
//                    text = "Click Me",
//                    style = MaterialTheme.typography.titleLarge.copy(
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold,
//                        fontStyle = FontStyle.Italic
//                    )
//                )

//                Text(
//                    text = "Styled Text",
//                    style = TextStyle(
//                        color = Color.Red,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        fontStyle = FontStyle.Italic,
//                        textAlign = TextAlign.Center
//                    )
//                )
            }
        }
    }
}
