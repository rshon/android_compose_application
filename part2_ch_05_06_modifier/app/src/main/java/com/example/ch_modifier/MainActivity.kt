package com.example.ch_modifier

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ch_modifier.ui.theme.Ch_modifierTheme
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ch_modifierTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    ClickTest()
//                    DragText()
//                    DragTextWithAnimation()
//                    SwipeableEx()
//                    ScrollableBoxExample()
//                    DetectTapPointerInputEx()
//                    DragPointerInputEx()
//                    ReversedFocusGridExample()
//                    OnKeyEventKeyEventBoxEx()
//                    LayoutModifierExamples()
//                    FillVsMatchParentSizeDemo()
//                    AllModifierExamples()
//                    AllDrawingModifierExamples()
                    AnimationModifierExamples()
                }
            }
        }
    }
}

@Composable
fun AnimationModifierExamples() {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues)
                    .verticalScroll(scrollState), // 세로 스크롤
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally // 예제들을 가운데 정렬
            ) {
                AnimateContentSizeExample()
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Spacer(modifier = Modifier.height(40.dp))

                GraphicsLayerModifierExample()
            }
        }
    )
}

@Composable
fun AnimateContentSizeExample() {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .animateContentSize()
                .clickable { expanded = !expanded }
                .padding(8.dp)
        ) {
            Text(
                text = if (expanded) {
                    "This one is very long sentence for showing that the animation effect is applied \n" +
                            "when you click or tatp the box!"
                } else {
                    "It's short!"
                }
            )
        }
    }
}

@Composable
fun GraphicsLayerModifierExample() {
    var isTransformed by remember { mutableStateOf(false) }

    Button(
        onClick = { isTransformed = !isTransformed },
        modifier = Modifier
            .graphicsLayer {
                scaleX = if (isTransformed) 1.3f else 1f
                scaleY = if (isTransformed) 1.3f else 1f
                rotationZ = if (isTransformed) -30f else 0f
                shadowElevation = if (isTransformed) 10f else 0f
                alpha = if (isTransformed) 0.5f else 1f
            }
            .padding(10.dp)
    ) {
        Text(
            text = if (isTransformed) "transformed" else "original",
            fontSize = 20.sp
        )
    }
}

@Composable
fun AllDrawingModifierExamples() {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues)
                    .verticalScroll(scrollState), // 세로 스크롤
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally // 예제들을 가운데 정렬
            ) {
                Text("drawBehind: 콘텐츠 뒤에 그리기", style = MaterialTheme.typography.titleMedium)
                DrawBehindExample()
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    "drawWithContent: 콘텐츠 앞/뒤에 그리고 제어",
                    style = MaterialTheme.typography.titleMedium
                )
                DrawWithContentExample()
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    "drawWithCache: 그리기 객체 캐싱 (성능 최적화)",
                    style = MaterialTheme.typography.titleMedium
                )
                DrawWithCacheExample()
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    )
}

@Composable
fun DrawBehindExample() {
    Text(
        text = "drawBehind",
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .size(150.dp, 80.dp)
            .background(Color.Yellow.copy(alpha = 0.5f))
            .padding(8.dp)
            .drawBehind {
                val padding = 0.dp.toPx()
                val innerWidth = size.width - 2 * padding
                val innerHeight = size.height - 2 * padding

                drawRect(
                    color = Color.Red,
                    topLeft = Offset(padding, padding),
                    size = androidx.compose.ui.geometry.Size(innerWidth, innerHeight),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.dp.toPx()
                    )
                )
            }
    )
}

@Composable
fun DrawWithContentExample() {
    Text(
        text = "drawWithContent",
        modifier = Modifier
            .size(150.dp, 80.dp)
            .drawWithContent {
                drawRect(color = Color.LightGray)

                drawContent()

                val strokeWidth = 2.dp.toPx()
                drawRect(
                    color = Color.Black,
                    style = Stroke(width = strokeWidth)
                )
            }
            .padding(8.dp)
    )
}

@Composable
fun DrawWithCacheExample() {
    Box(
        modifier = Modifier
            .size(150.dp, 80.dp)
            .drawWithCache {
                val circleRadius = size.minDimension / 2f

                onDrawBehind {
                    drawCircle(
                        color = Color.Blue.copy(alpha = 0.4f),
                        radius = circleRadius
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "drawWithCache")
    }
}

@Composable
fun AllModifierExamples() { // 전체 예제를 포함하도록 이름 변경
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState), // 세로 스크롤 추가
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 기존 6개 예제 호출 ---
        Text("1. fillMaxSize(): 부모의 최대 크기를 채움")
        FillMaxSizeExample()
        HorizontalDivider()

        Text("2. wrapContentSize(): 콘텐츠 크기만큼만 차지 (정렬과 함께 사용)")
        WrapContentSizeExample()
        HorizontalDivider()

        Text("3. padding(): 콘텐츠 주위에 내부 여백 추가")
        PaddingExample()
        HorizontalDivider()

        Text("4. aspectRatio(): 지정된 비율로 크기 조절 (한 축 크기 필요)")
        AspectRatioExample()
        HorizontalDivider()

        Text("5. align(): 부모 Scope 내에서 정렬 (부모가 더 커야 함)")
        AlignExample()
        HorizontalDivider()

        Text("6. matchParentSize(): 부모 Box의 크기와 동일하게 (BoxScope 내)")
        MatchParentSizeExample() // Color.Orange 수정된 버전 포함
        HorizontalDivider()

        // --- 새로운 예제들 ---
        Text("7. background(color, shape): 배경색과 모양 지정")
        BackgroundShapeExample()
        HorizontalDivider()

        Text("8. border(width, color, shape): 테두리 지정")
        BorderShapeExample()
        HorizontalDivider()

        Text("9. clip(shape): 내용물을 특정 모양으로 잘라냄")
        ClipShapeExample()
        HorizontalDivider()

        Text("10. shadow(elevation, shape, clip): 그림자 효과")
        ShadowShapeExample()
        HorizontalDivider()

        Text("11. alpha(alpha): 투명도 조절 (0f ~ 1f)")
        AlphaExample()
        HorizontalDivider()

        Text("12. rotate(degrees): 회전 (시계 방향)")
        RotateExample()
        HorizontalDivider()

        Text("13. graphicsLayer{...}: 복합적인 그래픽 효과 적용")
        GraphicsLayerExample()
        HorizontalDivider()
    }
}

@Composable
fun BackgroundShapeExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(color = Color.Green, shape = CircleShape)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Circle BG", color = Color.White)
    }
}

@Composable
fun BorderShapeExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(5.dp)
            .border(width = 2.dp, color = Color.Blue, shape = RoundedCornerShape(16.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Rounded Border")
    }
}

@Composable
fun ClipShapeExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.DarkGray)
            .clip(CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Clipped Content\nand Background", color=Color.White)
    }
}

@Composable
fun ShadowShapeExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp), clip = true)
            .background(Color.Yellow)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Shadow Box")
    }
}

@Composable
fun AlphaExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.LightGray)
            .alpha(0.5f),
        contentAlignment = Alignment.Center
    ) {
        Text("Alpha 50%", color = Color.Black)
    }
}

@Composable
fun RotateExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.Green)
            .rotate(30f),
        contentAlignment = Alignment.Center
    ) {
        Text("Rotated 30°")
    }
}

@Composable
fun GraphicsLayerExample() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    scaleX = 0.6f
                    scaleY = 1.2f
                    rotationY = 20f
                    cameraDistance = 7 * density
                }
                .background(Color.Yellow)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("graphicsLayer\nScale+RotateY", color = Color.Black, fontSize = 10.sp, textAlign = TextAlign.Center)
        }

        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    rotationZ = -50f
                    alpha = 0.5f
                    shadowElevation = 7.dp.toPx()
                    shape = RoundedCornerShape(8.dp)
                    clip = true
                }
                .background(Color.Blue)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("graphicsLayer\nRotate+Alpha+Shadow", color = Color.White, fontSize = 10.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun FillVsMatchParentSizeDemo() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(paddingValues),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("fillMaxSize Demo", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White)
                        ) {
                            Text("Parent Content", modifier = Modifier.padding(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Red.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    "fillMaxSize\n(fills max constraints)",
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    Text(
                        "↑ 빨간색(fillMaxSize)은\nParent(흰색) 영역을 넘어\nGrandparent(회색)가 허용한\n최대 영역까지 확장됨",
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("matchParentSize Demo", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .height(200.dp)
                            .width(200.dp)
                            .background(Color.Green),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .height(150.dp)
                                .width(150.dp)
                                .padding(10.dp)
                                .background(Color.White)
                        ) {
                            Text("Parent Content",
                                modifier = Modifier.padding(10.dp), color = Color.Black)

                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .matchParentSize()
                                    .background(Color.Blue.copy(alpha = 0.5f))
                                    .border(2.dp, Color.Black),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    "matchParentSize\n(matches parent's measured size)",
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    Text(
                        "↑ 파란색(matchParentSize)은\nParent(흰색)가 Text에 맞춰\n측정한 실제 크기만큼만\n정확히 채움",
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp
                    )
                }
            }
        }
    )
}

@Composable
fun LayoutModifierExamples() {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp) // 예제 사이 간격
            ) {
                Text("1. fillMaxSize(): 부모의 최대 크기를 채움")
                FillMaxSizeExample()
                HorizontalDivider()

                Text("2. wrapContentSize(): 콘텐츠 크기만큼만 차지 (정렬과 함께 사용)")
                WrapContentSizeExample()
                HorizontalDivider()

                Text("3. padding(): 콘텐츠 주위에 내부 여백 추가")
                PaddingExample()
                HorizontalDivider()

                Text("4. aspectRatio(): 지정된 비율로 크기 조절 (한 축 크기 필요)")
                AspectRatioExample()
                HorizontalDivider()

                Text("5. align(): 부모 Scope 내에서 정렬 (부모가 더 커야 함)")
                AlignExample()
                HorizontalDivider()

                Text("6. matchParentSize(): 부모 Box의 크기와 동일하게 (BoxScope 내)")
                MatchParentSizeExample()
        }
    })
}

@Composable
fun FillMaxSizeExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Green.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text("fillMaxSize")
        }
    }
}

@Composable
fun WrapContentSizeExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .background(Color.Yellow),
            contentAlignment = Alignment.Center
        ) {
            Text("wrap")
        }
    }
}

@Composable
fun PaddingExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
                .background(Color.Blue.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Text("padding(20.dp)")
        }
    }
}

@Composable
fun AspectRatioExample() {
    Box(
        modifier = Modifier
            .height(100.dp)
            .background(Color.LightGray)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(16f / 9f)
                .background(Color.Red.copy(alpha = 0.7f))
                .align(Alignment.Center)
        ) {
            Text("aspectRatio")
        }
    }
}

@Composable
fun AlignExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.LightGray),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.Red)
                .align(Alignment.BottomEnd)
        ) {
            Text("align(BE)")
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.Cyan)
                .align(Alignment.TopStart)
        ) {
            Text("align(TS)")
        }
    }
}

@Composable
fun MatchParentSizeExample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.LightGray)
    ) {
        Text("Parent Content", Modifier.align(Alignment.TopCenter))

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFFFFA500).copy(alpha = 0.5f))
        ) {
            Text("Matches Parent Size", Modifier.align(Alignment.BottomCenter))
        }
    }
}

private val MoveUnit = 10.dp

@Composable
fun OnKeyEventKeyEventBoxEx() {
    var message by remember { mutableStateOf("press keys") }
    var offsetX by remember { mutableStateOf(0.dp) }
    var offsetY by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionUp -> {
                            message = "Up"
                            offsetY -= MoveUnit
                        }

                        Key.DirectionDown -> {
                            message = "down"
                            offsetY += MoveUnit
                        }

                        Key.DirectionLeft -> {
                            message = "left"
                            offsetX -= MoveUnit
                        }

                        Key.DirectionRight -> {
                            message = "right"
                            offsetX += MoveUnit
                        }

                        Key.Enter -> message = "enter"
                        else -> return@onKeyEvent false
                    }
                    true  // event is consumed here
                } else {
                    false
                }
            }
            .focusable()  // use it for handling keyboard event
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(80.dp)
                .background(Color.Green)
                .border(2.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(text = message,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ReversedFocusGridExample() {
    val (fr1, fr2, fr3, fr4) = remember { FocusRequester.createRefs() }
    val focusStates = remember { mutableStateMapOf(0 to false, 1 to false, 2 to false, 3 to false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FocusableBox(
                    text = "1",
                    focusRequester = fr1,
                    isFocused = focusStates[0] ?: false,
                    modifier = Modifier
                        .focusProperties {
                            next = fr4
                            previous = fr2
                            right = fr2
                            down = fr3
                        }
                        .onFocusChanged { focusState ->
                            focusStates[0] = focusState.isFocused
                        }
                )
                FocusableBox(
                    text = "2",
                    focusRequester = fr2,
                    isFocused = focusStates[1] ?: false,
                    modifier = Modifier
                        .focusProperties {
                            next = fr1
                            previous = fr3
                            left = fr1
                            down = fr4
                        }
                        .onFocusChanged { focusState ->
                            focusStates[1] = focusState.isFocused
                        }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FocusableBox(
                    text = "3",
                    focusRequester = fr3,
                    isFocused = focusStates[2] ?: false,
                    modifier = Modifier
                        .focusProperties {
                            next = fr2
                            previous = fr4
                            right = fr4
                            up = fr1
                        }
                        .onFocusChanged { focusState ->
                            focusStates[2] = focusState.isFocused
                        }
                )
                FocusableBox(
                    text = "4",
                    focusRequester = fr4,
                    isFocused = focusStates[3] ?: false,
                    modifier = Modifier
                        .focusProperties {
                            next = fr3
                            previous = fr1
                            left = fr3
                            up = fr2
                        }
                        .onFocusChanged { focusState ->
                            focusStates[3] = focusState.isFocused
                        }
                )
            }
        }

        LaunchedEffect(Unit) {
            fr1.requestFocus()
        }
    }
}

@Composable
fun FocusableBox(
    text: String,
    focusRequester: FocusRequester,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    val elevation = if (isFocused) 8.dp else 2.dp
    val backgroundColor = if (isFocused) Color.Yellow else Color.LightGray
    val borderColor = if (isFocused) Color.Red else Color.Transparent
    val shape = RoundedCornerShape(8.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = { focusRequester.requestFocus() }
            )
            .size(100.dp),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(2.dp, borderColor),
        tonalElevation = elevation
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, fontSize = 24.sp)
        }
    }
}

private val BoxSize = 80.dp

@Composable
fun DragPointerInputEx() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current

        val boxWidthPx = with(density) { BoxSize.toPx() }
        val boxHeightPx = with(density) { BoxSize.toPx() }

        val parentWidthPx = with(density) { maxWidth.toPx() }
        val parentHeightPx = with(density) { maxHeight.toPx() }

        val centerX = (parentWidthPx - boxWidthPx) / 2f
        val centerY = (parentHeightPx - boxHeightPx) / 2f

        var offsetX by remember { mutableStateOf(centerX) }
        var offsetY by remember { mutableStateOf(centerY) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val pointerId = down.id

                            while (true) {
                                val event = awaitPointerEvent()
                                val dragChange = event.changes.firstOrNull { it.id == pointerId }

                                if (dragChange == null || dragChange.changedToUp()) {
                                    break
                                }

                                offsetX += dragChange.positionChange().x
                                offsetY += dragChange.positionChange().y
                                dragChange.consume()
                            }
                        }
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                    }
                    .size(BoxSize)
                    .background(Color.Green)
            )
        }
    }
}


@Composable
fun DetectTapPointerInputEx() {
    var textState by remember { mutableStateOf("Tap Me!") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        textState = "Single Tap"
                    },
                    onDoubleTap = {
                        textState = "Double Tap"
                    },
                    onLongPress = {
                        textState = "Long Tap"
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = textState,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ScrollableBoxExample() {
    var yOffset by remember { mutableStateOf(0f) }
    val scrollableState = rememberScrollableState { delta ->
        yOffset += delta
        delta // consumed
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->

            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(0, yOffset.toInt()) }
                                .size(100.dp)
                                .background(Color.LightGray)
                                .scrollable(
                                    state = scrollableState,
                                    orientation = Orientation.Vertical
                                )
                        )
                    }
                }
            }
        }
    )
}

enum class LockState {
    Locked,
    Unlocked
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeableEx() {
    val swipeableState = rememberSwipeableState(initialValue = LockState.Locked)

    // 앵커(anchors): 0f(왼쪽) → Locked, 300f(오른쪽) → Unlocked 로 스냅
    // 실제로 화면 크기에 맞추어 원하는 거리를 설정해도 됩니다.
    val anchors = mapOf(
        0f to LockState.Locked,
        300f to LockState.Unlocked
    )

    val isUnlocked = (swipeableState.currentValue == LockState.Unlocked)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = swipeableState.offset.value.roundToInt().dp)
                    .size(width = 120.dp, height = 60.dp)
                    .clip(RoundedCornerShape(30.dp)) // 양 끝 둥글게
                    .background(
                        if (isUnlocked) Color.Green
                        else Color.White.copy(alpha = 0.85f)
                    )
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        orientation = Orientation.Horizontal,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) }, // 30% 이상 드래그 시 다음 상태로
                        velocityThreshold = 200.dp
                    )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isUnlocked) "✔" else "→",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) Color.White else Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isUnlocked) "Unlocked!" else "Slide to Unlock",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Composable
fun DragTextWithAnimation() {
    var offsetX by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val maxWidth = with(density) {
        configuration.screenWidthDp.dp.toPx() - 100.dp.toPx()
    }

    var isDragging by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.5f else 1f,     // scale-out 50 % while dragging
        animationSpec = tween(durationMillis = 200), label = "scale"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isDragging) Color.Green else Color.Blue,
        animationSpec = tween(durationMillis = 200), label = "color"
    )

    val bounceAnimatable = remember { Animatable(0f) }

    val draggableState = rememberDraggableState { delta ->
        offsetX = (offsetX + delta).coerceIn(0f, maxWidth)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .graphicsLayer(
                            translationX = offsetX + bounceAnimatable.value,
                            scaleX = scale,
                            scaleY = scale
                        )
                        .size(100.dp)
                        .background(backgroundColor)
                        .draggable(
                            state = draggableState,
                            orientation = Orientation.Horizontal,
                            onDragStarted = {
                                isDragging = true
                            },
                            onDragStopped = { velocity ->
                                isDragging = false
                                bounceAnimatable.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMedium,
                                        visibilityThreshold = 0.1f
                                    ),
                                    initialVelocity = velocity / 10f // 드래그 속도 반영
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Drag Me",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    )
}

@Composable
fun DragText() {
    var offsetX by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val maxWidth = with(density) {
        configuration.screenWidthDp.dp.toPx() - 100.dp.toPx()
    }

    val draggableState = rememberDraggableState { delta ->
        offsetX = (offsetX + delta).coerceIn(0f, maxWidth)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .graphicsLayer(translationX = offsetX)
                        .size(100.dp)
                        .background(Color.Blue)
                        .draggable(
                            state = draggableState,
                            orientation = Orientation.Horizontal
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Drag Me",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClickTest() {
    var clickCount by remember { mutableStateOf(0) }
    var combinedClickState by remember { mutableStateOf("click here!") }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)          // padding at the top as much as paddingValues
                .padding(top = 50.dp),           // padding 50.dp at the top
            contentAlignment = Alignment.Center  // central alignment
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (clickCount > 0) "$clickCount" else "click me!",
                    fontSize = 40.sp,
                    modifier = Modifier.clickable {
                        clickCount += 1
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .combinedClickable(
                            onClick = { combinedClickState = "clicked" },
                            onDoubleClick = { combinedClickState = "double clicked" },
                            onLongClick = { combinedClickState = "long clicked" }
                        )
                ) {
                    Text(
                        text = combinedClickState,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

