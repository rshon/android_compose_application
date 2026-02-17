package com.example.mediaapplication.ui

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mediaapplication.viewmodel.MediaViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(
    viewModel: MediaViewModel,
    filePath: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isPlaying by viewModel.isPlaying
    val progress by viewModel.progress
    val duration by viewModel.duration
    val currentMediaItem by viewModel.currentMediaItem // 현재 재생 중인 MediaItem

    LaunchedEffect(filePath) {
        println("Playing music with filePath: $filePath")
        viewModel.playMusic(com.example.mediaapplication.data.model.MediaItem(
            type = com.example.mediaapplication.data.model.MediaType.MUSIC,
            title = File(filePath).name,
            filePath = filePath
        ))
    }

    // 현재 재생 중인 곡의 file Path
    val currentFilePath by derivedStateOf {
        currentMediaItem?.filePath ?: filePath
    }

    // 현재 곡이 변경될 때마다 로그 출력
    LaunchedEffect(currentFilePath) {
        println("Current filePath updated to: $currentFilePath")
        println("UI re-render triggered with progress: $progress")
    }

    // 앨범 아트 추출
    val bitmap = remember(currentFilePath) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(currentFilePath)
            val art = retriever.embeddedPicture
            retriever.release()
            art?.let { BitmapFactory.decodeByteArray(it, 0, it.size) } ?: run {
                BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_dialog_alert)
            }
        } catch (e: Exception) {
            println("Error loading album art: ${e.message}")
            BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_dialog_alert)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Player") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .border(2.dp, Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(if (bitmap == null) Color.Gray else Color.Transparent)
            ) {
                Image(
                    bitmap = (bitmap ?: BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_dialog_alert)).asImageBitmap(),
                    contentDescription = "Album Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = progress.toFloat(),
                onValueChange = { newValue -> viewModel.seekTo(newValue.toInt()) },
                valueRange = 0f..(duration.toFloat().coerceAtLeast(1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(modifier = Modifier.size(60.dp).border(1.dp, Color.Black)) {
                    IconButton(onClick = { viewModel.playPrevious(context) }) {
                        Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.Black)
                    }
                }
                Box(modifier = Modifier.size(60.dp).border(1.dp, Color.Black)) {
                    IconButton(onClick = { if (isPlaying) viewModel.pauseMusic() else viewModel.resumeMusic() }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black
                        )
                    }
                }
                Box(modifier = Modifier.size(60.dp).border(1.dp, Color.Black)) {
                    IconButton(onClick = { viewModel.playNext(context) }) {
                        Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = { /* 셔플 */ },
                modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Replay, contentDescription = "Shuffle", tint = Color.Black)
            }
        }
    }
}