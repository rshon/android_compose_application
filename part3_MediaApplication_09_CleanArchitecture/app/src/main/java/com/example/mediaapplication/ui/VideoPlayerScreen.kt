package com.example.mediaapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import com.example.mediaapplication.constants.PlayerConstants
import com.example.mediaapplication.domain.model.PlayerState
import com.example.mediaapplication.ui.video.PlayerRenderer
import com.example.mediaapplication.viewmodel.VideoPlayerViewModel

private const val TAG = "VideoPlayerScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoPlayerViewModel: VideoPlayerViewModel,
    navController: NavController,
    uri: String,
) {
    val scope = rememberCoroutineScope()
    var isFullScreen by rememberSaveable { mutableStateOf(false) }
    var showControls by rememberSaveable { mutableStateOf(true) }

    val uiState by videoPlayerViewModel.uiState.collectAsState()

    // hide controller automatically
    LaunchedEffect(showControls, uiState.playerState.isPlaying) {
        if (showControls && uiState.playerState.isPlaying) {
            delay(PlayerConstants.AUTO_HIDING_MS)
            showControls = false
        }
    }

    // new content
    LaunchedEffect(uri) {
        videoPlayerViewModel.play(uri, scope)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { showControls = !showControls }
    ) {
        // layer 1: video content
        PlayerRenderer(videoPlayerViewModel, modifier = Modifier.fillMaxSize())

        // layer 2: media controller
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // back button
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.NavigateBefore, "Back", tint = Color.White)
                    }
                    IconButton(onClick = { isFullScreen = !isFullScreen }) {
                        Icon(
                            if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            "Toggle Fullscreen",
                            tint = Color.White
                        )
                    }
                }

                // bottom control
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    // progress bar
                    VideoPlayerControls(
                        playerState = uiState.playerState,
                        onSeek = { position -> videoPlayerViewModel.seekTo(position) }
                    )

                    // time indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatMilliseconds(uiState.playerState.currentPosition), color = Color.White, style = MaterialTheme.typography.bodySmall)
                        Text(formatMilliseconds(uiState.playerState.duration), color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // playback control buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { videoPlayerViewModel.playPrevious() }) {
                            Icon(Icons.Default.SkipPrevious, "Previous", tint = Color.White)
                        }
                        IconButton(
                            onClick = {
                                if (uiState.playerState.isPlaying) videoPlayerViewModel.pause()
                                else videoPlayerViewModel.resume()
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                if (uiState.playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        IconButton(onClick = { videoPlayerViewModel.playNext() }) {
                            Icon(Icons.Default.SkipNext, "Next", tint = Color.White)
                        }
                    }
                }
            }
        }

        // layer 3: loading indicator
        if (uiState.isLoading) {
            Log.d(TAG, "is loading")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // layer 4: error dialog
        uiState.error?.let { errorMessage ->
            ErrorDialog(
                message = errorMessage,
                onRetry = { videoPlayerViewModel.retry() },
                onDismiss = { videoPlayerViewModel.clearError() }
            )
        }
    }
}

@Composable
fun VideoPlayerControls(
    playerState: PlayerState,
    onSeek: (Long) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0f) }

    val sliderPosition = if (isDragging) {
        dragPosition
    } else {
        playerState.currentPosition.toFloat()
    }

    Slider(
        value = sliderPosition,
        onValueChange = {
            isDragging = true
            dragPosition = it
        },
        onValueChangeFinished = {
            onSeek(dragPosition.toLong())
            isDragging = false
        },
        valueRange = 0f..playerState.duration.toFloat().coerceAtLeast(0f),

        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.Red,
            inactiveTrackColor = Color.Gray
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ErrorDialog(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Playback error")
        },
        text = {
            Text(message)
        },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text("Retry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
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