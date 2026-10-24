package com.example.mediaapplication.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.mediaapplication.viewmodel.MediaViewModel
import com.example.mediaapplication.viewmodel.VideoPlayerViewModel
import kotlinx.coroutines.delay

private const val TAG = "VideoPlayerScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    viewModel: MediaViewModel,
    navController: NavController,
    uri: String,
    onBack: () -> Unit,
    videoPlayerViewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var isFullScreen by rememberSaveable { mutableStateOf(false) }
    var showControls by rememberSaveable { mutableStateOf(true) }

    val playerState by videoPlayerViewModel.playerState.collectAsState()

    val exoPlayer = videoPlayerViewModel.getPlayer()

    Log.d(TAG, "VideoPlayerScreen")

    // Auto-hide controls after 3s
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }

    // Play on enter
    LaunchedEffect(uri) {
        videoPlayerViewModel.play(uri, scope)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video view
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // custom control 사용으로 ExoPlayer control 사용 X
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable { showControls = !showControls }
        )

        if (showControls) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    println("back")
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.NavigateBefore,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                IconButton(onClick = {
                    println("full screen")
                    isFullScreen = !isFullScreen
                }) {
                    Icon(
                        imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Toggle Fullscreen",
                        tint = Color.White
                    )
                }
            }

            // Bottom Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(Color(0x80000000), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // 재생 바 (Slider)
                Slider(
                    value = playerState.currentPosition.toFloat(),
                    onValueChange = { newValue ->
                        // 사용자가 슬라이더를 드래그할 때 컨트롤을 계속 표시합니다.
                        showControls = true
                        videoPlayerViewModel.seekTo(newValue.toLong())
                    },
                    valueRange = 0f..playerState.duration.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Red,
                        inactiveTrackColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // 시간 표시
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 현재 재생 시간
                    Text(
                        text = formatMilliseconds(playerState.currentPosition),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    // 남은 시간
                    Text(
                        text = formatMilliseconds(playerState.duration - playerState.currentPosition),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        println("previous")
                        videoPlayerViewModel.previous()
                    }) {
                        Icon(
                            imageVector = Icons.Default.NavigateBefore,
                            contentDescription = "Previous",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        println("play/pause toggle")
                        if (playerState.isPlaying) { // playerState.isPlaying 값에 따라 아이콘과 동작 변경
                            videoPlayerViewModel.pause()
                        } else {
                            videoPlayerViewModel.resume()
                        }
                    }) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp) // 좀 더 크게
                        )
                    }
                    IconButton(onClick = {
                        println("Next")
                        videoPlayerViewModel.next()
                    }) {
                        Icon(
                            imageVector = Icons.Default.NavigateNext,
                            contentDescription = "Next",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

fun formatMilliseconds(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
