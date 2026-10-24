package com.example.ch_canvas_

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height  // height Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset  // Offset 좌표
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size  // Size 크기
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color  // Color 클래스
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp  // dp 단위
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ch_canvas_.ui.theme.Ch_canvas_Theme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Random
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ch_canvas_Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
//                        BarChart(listOf(2.0f, 3.0f, 2.45f, 2.1f, 2.2f, 2.9f, 3.2f, 3.1f, 2.5f))
//                        ParticleSystem()
//                        CurvedButton("string", onClick = {})
//                        WaveAnimation()
//                        CameraWithOverlay()
//                        CurvedButton(
//                            "test", Modifier,
//                            onClick = {}
//                        )

//                        AnalogClock()
//                        DigitalClock()
                        MapWithCanvasOverlay()
                    }
                }
            }
        }
    }
}

@Composable
fun MapWithCanvasOverlay() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(37.5665, 126.9780),  // Seoul coordinate
            14f
        )
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState
        )

        CustomMapCanvasOverlay(
            modifier = Modifier.matchParentSize(),
            markerLatLng = LatLng(37.5665, 126.9780),
            cameraPositionState = cameraPositionState
        )
    }
}

@Composable
fun CustomMapCanvasOverlay(
    modifier: Modifier = Modifier,
    markerLatLng: LatLng,
    cameraPositionState: CameraPositionState
) {
    val projection = cameraPositionState.projection ?: return
    val screenLocation = projection.toScreenLocation(markerLatLng)
    val markerOffset = Offset(screenLocation.x.toFloat(), screenLocation.y.toFloat())

    val density = LocalDensity.current
    val smallRadiusPx = with(density) { 8.dp.toPx() }
    val gradientRadiusPx = with(density) { 24.dp.toPx() }
    val arrowTextOffsetPx = with(density) { 40.dp.toPx() }

//    val arrowLengthPx = with(density) { 12.dp.toPx() }
//    val textMarginPx = with(density) { 4.dp.toPx() }

    val red = Color(0xFFFF0000)
    val neonGreen = Color(0xFF00FF00)

    Canvas(modifier = modifier) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Red.copy(alpha = 0f)),
                center = markerOffset,
                radius = gradientRadiusPx
            ),
            center = markerOffset,
            radius = gradientRadiusPx
        )

        drawCircle(
            color = Color.Red,
            center = markerOffset,
            radius = smallRadiusPx
        )

        val text = "Selected point"
        val textPaint = android.graphics.Paint().apply {
            color = red.toArgb()
            textSize = with(density) { 14.dp.toPx() }
            isAntiAlias = true
        }

        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = markerOffset - Offset(arrowTextOffsetPx, arrowTextOffsetPx + textHeight)
        val textBaselineY = textOffset.y - textPaint.ascent()

//        // 5) 화살표 시작점(shaft의 start)은 텍스트 바로 아래
//        val arrowStart = Offset(textOffset.x, textOffset.y + textHeight + textMarginPx)
//
//        // 6) 화살표 축(shaft)
//        drawLine(
//            color = neonGreen,
//            start = arrowStart,
//            end = markerOffset,
//            strokeWidth = with(density) { 2.dp.toPx() }
//        )
//
//        // 7) 화살표 머리(head)
//        val angle = atan2(
//            markerOffset.y - arrowStart.y,
//            markerOffset.x - arrowStart.x
//        )
//        val headAngleOffset = Math.toRadians(20.0)
//        val angles = listOf(angle + headAngleOffset, angle - headAngleOffset)
//        angles.forEach { a ->
//            val hx = markerOffset.x - cos(a).toFloat() * arrowLengthPx
//            val hy = markerOffset.y - sin(a).toFloat() * arrowLengthPx
//            drawLine(
//                color = neonGreen,
//                start = markerOffset,
//                end = Offset(hx, hy),
//                strokeWidth = with(density) { 2.dp.toPx() }
//            )
//        }

        drawContext.canvas.nativeCanvas.drawText(
            text,
            textOffset.x,
            textBaselineY,
            textPaint
        )
    }
}

@Composable
fun CustomMapCanvasOverlaySimple(
    modifier: Modifier,
    markerLatLng: LatLng,
    cameraPositionState: CameraPositionState
) {
    val projection = cameraPositionState.projection ?: return

    // 위도/경도 → 화면 좌표 (픽셀 좌표)로 변환
    val screenLocation = projection.toScreenLocation(markerLatLng)
    val markerOffset = Offset(screenLocation.x.toFloat(), screenLocation.y.toFloat())

    Canvas(modifier = Modifier.fillMaxSize()) {
        // 마커: 원 그리기
        drawCircle(
            color = Color.Red,
            center = markerOffset,
            radius = 12f
        )

        // 텍스트, 선, 기타 요소도 여기에 추가 가능
    }
}

@Composable
fun DigitalClock(
    modifier: Modifier = Modifier
        .size(width = 200.dp, height = 80.dp)
        .padding(16.dp),
    textColor: Color = Color.Black,
    fontSize: TextUnit = 32.sp,
    fontWeight: FontWeight = FontWeight.Bold
) {
    var timeString by remember { mutableStateOf("") }
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm:ss") }
    LaunchedEffect(Unit) {
        while (true) {
            timeString = LocalTime.now().format(formatter)
            delay(1000L)
        }
    }

    Canvas(modifier) {
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                color = textColor.toArgb()
                textSize = fontSize.toPx()
                this.isFakeBoldText = fontWeight == FontWeight.Bold
                textAlign = android.graphics.Paint.Align.CENTER
            }
            // adjusts baseline
            val x = size.width / 2f
            val y = size.height / 2f - (paint.descent() + paint.ascent()) / 2f
            drawText(timeString, x, y, paint)
        }
    }
}

@Composable
fun AnalogClock(
    modifier: Modifier = Modifier
        .size(200.dp)
        .padding(16.dp),
    tickColor: Color = Color.Black,
    hourHandColor: Color = Color.Black,
    minuteHandColor: Color = Color.DarkGray,
    secondHandColor: Color = Color.Red
) {
    var time: LocalTime by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            time = LocalTime.now()
            delay(1000L)
        }
    }

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = min(cx, cy)

        // clock boundary
        drawCircle(
            color = tickColor,
            radius = radius,
            style = Stroke(width = 4.dp.toPx())
        )

        // 12 ticks
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30).toDouble() - 90)
            val outer = Offset(
                ((cx + cos(angle) * radius).toFloat()),
                ((cy + sin(angle) * radius).toFloat())
            )
            val inner = Offset(
                ((cx + cos(angle) * (radius * 0.9f)).toFloat()),
                ((cy + sin(angle) * (radius * 0.9f)).toFloat())
            )
            drawLine(
                color = tickColor,
                start = inner,
                end = outer,
                strokeWidth = 3.dp.toPx()
            )
        }

        // angles
        val secondAngle = (time.second * 6f - 90f).toDouble().let { Math.toRadians(it) }
        val minuteAngle = ((time.minute + time.second / 60f) * 6f - 90f).toDouble().let { Math.toRadians(it) }
        val hourAngle = (((time.hour % 12) + time.minute / 60f) * 30f - 90f).toDouble().let { Math.toRadians(it) }

        // Hour Hand
        drawLine(
            color = hourHandColor,
            start = Offset(cx, cy),
            end = Offset(
                (cx + cos(hourAngle) * radius * 0.5f).toFloat(),
                (cy + sin(hourAngle) * radius * 0.5f).toFloat()
            ),
            strokeWidth = 6.dp.toPx()
        )

        // Minute Hand
        drawLine(
            color = minuteHandColor,
            start = Offset(cx, cy),
            end = Offset(
                (cx + cos(minuteAngle) * radius * 0.7f).toFloat(),
                (cy + sin(minuteAngle) * radius * 0.7f).toFloat()
            ),
            strokeWidth = 4.dp.toPx()
        )

        // Second Hand
        drawLine(
            color = secondHandColor,
            start = Offset(cx, cy),
            end = Offset(
                (cx + cos(secondAngle) * radius * 0.8f).toFloat(),
                (cy + sin(secondAngle) * radius * 0.8f).toFloat()
            ),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
fun CameraWithOverlay(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier) {
        // ① CameraPreview를 AndroidView로 띄우기 (구현 생략)
        AndroidView(factory = { ctx ->
            // SurfaceView나 CameraX PreviewView 세팅
            PreviewView(ctx).apply { /* ... */ }
        }, modifier = Modifier.matchParentSize())

        // ② Canvas로 오버레이
        Canvas(Modifier.matchParentSize()) {
            val stroke = Stroke(width = 4.dp.toPx())
            val rect = Rect(
                left = size.width * 0.1f,
                top = size.height * 0.2f,
                right = size.width * 0.9f,
                bottom = size.height * 0.8f
            )
            drawRect(
                color = Color.White.copy(alpha = 0.6f),
                topLeft = rect.topLeft,
                size = rect.size,
                style = stroke
            )
            drawLine(
                color = Color.Green,
                start = rect.topLeft,
                end = rect.bottomRight,
//                style = stroke
            )
        }
    }
}

@Composable
fun WaveAnimation(modifier: Modifier = Modifier.fillMaxSize()) {
    val infinite = rememberInfiniteTransition()
    val phase by infinite.animateFloat(
        initialValue = 0f, targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    Canvas(modifier) {
        val amplitude = 30f
        val wavelength = size.width / 3
        val centerY = size.height / 2

        val path = Path().apply {
            moveTo(0f, centerY)
            var x = 0f
            while (x <= size.width) {
                val y = centerY + amplitude * sin((x / wavelength * 2 * Math.PI + phase))
                lineTo(x, y.toFloat())
                x += 1f
            }
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, color = Color(0xFF03A9F4))
    }
}

@Composable
fun CurvedButton(
    text: String,
    modifier: Modifier = Modifier
        .padding(16.dp)
        .size(width = 200.dp, height = 60.dp),
    onClick: () -> Unit
) {
    Canvas(modifier.clickable(onClick = onClick)) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.5f)
            cubicTo(
                x1 = size.width * 0.25f,
                y1 = 0f,
                x2 = size.width * 0.75f,
                y2 = size.height,
                x3 = size.width,
                y3 = size.height * 0.5f
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, color = Color.Yellow)
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Black)
    }
}

@Composable
fun ParticleSystem(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
) {
    val particles = remember { List(100) { Particle.random() } }
    val infinite = rememberInfiniteTransition()
    val progress by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    Canvas(modifier) {
        particles.forEach { p ->
            val x = p.start.x + (p.end.x - p.start.x) * progress
            val y = p.start.y + (p.end.y - p.start.y) * progress
            drawCircle(
                color = p.color,
                radius = p.size * (1 - progress),
                center = Offset(x, y)
            )
        }
    }
}

private data class Particle(
    val start: Offset,
    val end: Offset,
    val size: Float,
    val color: Color
) {
    companion object {
        fun random(): Particle {
            val rnd = Random()
            return Particle(
                start = Offset(rnd.nextFloat() * 400, rnd.nextFloat() * 800),
                end = Offset(rnd.nextFloat() * 400, rnd.nextFloat() * 800),
                size = rnd.nextFloat() * 8 + 2,
                color = Color(
                    red = rnd.nextFloat(),
                    green = rnd.nextFloat(),
                    blue = rnd.nextFloat(),
                    alpha = 0.8f
                )
            )
        }
    }
}

@Composable
fun BarChart(
    data: List<Float>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp)
) {
    Canvas(modifier) {
        val barWidth = size.width / (data.size * 2f)
        val maxValue = data.maxOrNull() ?: 1f

        data.forEachIndexed { idx, value ->
            val left = idx * 2 * barWidth + barWidth / 2
            val top = size.height * (1 - (value / maxValue))
            drawRect(
                color = Color(0xFF4CAF50),
                topLeft = Offset(left, top),
                size = Size(barWidth, size.height - top)
            )
        }
    }
}
