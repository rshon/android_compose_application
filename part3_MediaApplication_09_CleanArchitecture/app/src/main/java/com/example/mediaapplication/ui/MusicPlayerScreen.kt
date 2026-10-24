package com.example.mediaapplication.ui

import android.annotation.SuppressLint
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
import com.example.mediaapplication.domain.model.MediaItem
import com.example.mediaapplication.domain.model.MediaType
import com.example.mediaapplication.viewmodel.MediaViewModel
import java.io.File

@SuppressLint("UnrememberedMutableState")
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
    val currentMediaItem by viewModel.currentMediaItem
    val isShuffled by viewModel.isShuffled

    LaunchedEffect(filePath) {
        println("Playing music with filePath: $filePath")
        viewModel.playMusic(
            MediaItem(
                type = MediaType.MUSIC,
                title = File(filePath).name,
                filePath = filePath
            )
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            println("MusicPlayerScreen disposed - pausing music")
            viewModel.pauseMusic()
        }
    }

    val currentFilePath by derivedStateOf {
        currentMediaItem?.filePath ?: filePath
    }

    // logging when file is changed
    LaunchedEffect(currentFilePath) {
        println("Current filePath updated to: $currentFilePath")
        println("UI re-render triggered with progress: $progress")
    }

    // Extracting album art
    val bitmap = remember(currentFilePath) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(currentFilePath)
            val art = retriever.embeddedPicture
            retriever.release()
            println("extracting album art...$currentFilePath")
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
                    bitmap = (bitmap ?: BitmapFactory.decodeResource(
                        context.resources, android.R.drawable.ic_dialog_alert)).asImageBitmap(),
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
                    IconButton(onClick = { viewModel.playPrevious() }) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = Color.Black
                        )
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
                    IconButton(onClick = { viewModel.playNext() }) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = { viewModel.toggleShuffle() },
                modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = if (isShuffled) Icons.Default.Shuffle else Icons.Default.Replay,
                    contentDescription = if (isShuffled) "Disable Shuffle" else "Enable Shuffle",
                    tint = Color.Black
                )
            }
        }
    }
}
