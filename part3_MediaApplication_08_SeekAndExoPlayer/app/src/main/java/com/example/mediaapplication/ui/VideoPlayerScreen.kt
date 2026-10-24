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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mediaapplication.ui.videoplayer.PlayerRenderer
import com.example.mediaapplication.viewmodel.MediaViewModel
import com.example.mediaapplication.viewmodel.PlayerState
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

    Log.d(TAG, "VideoPlayerScreen")

    // Auto-hide controls
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(4000)
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
            .clickable { showControls = !showControls }
    ) {
        PlayerRenderer(videoPlayerViewModel, modifier = Modifier.fillMaxSize())

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

            // Bottom Controls - 개선된 컨트롤 사용
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(Color(0x80000000), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // 개선된 progress slider 사용
                ImprovedVideoPlayerControls(
                    playerState = playerState,
                    onSeek = { position ->
                        videoPlayerViewModel.seekTo(position)
                    },
                    onShowControls = {
                        showControls = true
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // elapsed time
                    Text(
                        text = formatMilliseconds(playerState.currentPosition),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    // remaining time
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
                        if (playerState.isPlaying) {
                            videoPlayerViewModel.pause()
                        } else {
                            videoPlayerViewModel.resume()
                        }
                    }) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
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

// 개선된 Slider 컨트롤 컴포넌트
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedVideoPlayerControls(
    playerState: PlayerState,
    onSeek: (Long) -> Unit,
    onShowControls: () -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0L) }

    // Slider의 현재 값 (드래그 중이면 드래그 위치, 아니면 실제 재생 위치)
    val sliderValue = if (isDragging) dragPosition else playerState.currentPosition

    // 드래그 시작 시 현재 위치로 초기화
    LaunchedEffect(isDragging) {
        if (isDragging && dragPosition == 0L) {
            dragPosition = playerState.currentPosition
        }
    }

    Slider(
        value = sliderValue.toFloat(),
        onValueChange = { newValue ->
            if (!isDragging) {
                isDragging = true
            }
            dragPosition = newValue.toLong()
            onShowControls() // 컨트롤 표시 유지
        },
        onValueChangeFinished = {
            // 드래그 완료 시에만 실제 seek 수행
            if (isDragging) {
                isDragging = false
                onSeek(dragPosition)
                Log.d("VideoPlayerControls", "Seek to $dragPosition")
            }
        },
        valueRange = 0f..playerState.duration.toFloat().coerceAtLeast(1f),
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.Red,
            inactiveTrackColor = Color.Gray
        ),
        modifier = Modifier.fillMaxWidth()
    )
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
