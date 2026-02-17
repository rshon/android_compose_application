package com.example.mediaapplication.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.example.mediaapplication.domain.model.MediaItem
import com.example.mediaapplication.domain.model.MediaType
import com.example.mediaapplication.viewmodel.MediaDataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaContentListScreen(
    mediaDataViewModel: MediaDataViewModel,
    onImageClick: (String) -> Unit,
    onMusicClick: (String) -> Unit,
    onVideoClick: (String) -> Unit
) {
    val mediaItems by mediaDataViewModel.mediaItems
    val hasPermissions by mediaDataViewModel.hasPermissions
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            mediaDataViewModel.hasPermissions.value = permissions.values.all { it }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
            launcher.launch(permissions)
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            launcher.launch(permissions)
            mediaDataViewModel.hasPermissions.value = true  // Android 12 이하 버전에서는 권한 필요 없음
        }
    }

    LaunchedEffect(hasPermissions) {
        if (hasPermissions) {
            mediaDataViewModel.loadMediaContent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Media Content") })
        }
    ) { padding ->
        val listState = rememberLazyListState()
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(8.dp)
            ) {
                items(mediaItems.size) { index ->
                    MediaItemRow(
                        item = mediaItems[index],
                        onImageClick = onImageClick,
                        onMusicClick = onMusicClick,
                        onVideoClick = onVideoClick
                    )
                }
            }

            if (mediaItems.isNotEmpty()) {
                CustomScrollbar(
                    listState = listState,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            if (mediaItems.isEmpty()) {
                Text(
                    text = "No media items available",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CustomScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val itemCount by remember {
        derivedStateOf { listState.layoutInfo.totalItemsCount }
    }

    val scrollPosition by remember {
        derivedStateOf {
            val firstVisibleItem = listState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
            val viewportSize = listState.layoutInfo.viewportSize.height

            if (itemCount > 0) {
                val visibleItemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull()
                val itemSize = visibleItemInfo?.size ?: viewportSize

                if (itemSize > 0) {
                    (firstVisibleItem + firstVisibleItemScrollOffset.toFloat() / itemSize) / itemCount
                } else {
                    firstVisibleItem.toFloat() / kotlin.math.max(1, itemCount)
                }
            } else {
                0f
            }
        }
    }

    val viewportSize by remember {
        derivedStateOf { listState.layoutInfo.viewportSize.height }
    }

    if (itemCount <= 0) return

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(8.dp)
            .padding(vertical = 4.dp)
    ) {
        val scrollbarHeight = with(LocalDensity.current) {
            (viewportSize.toFloat() / kotlin.math.max(itemCount, 1)).toDp()
        }

        val scrollbarOffset = with(LocalDensity.current) {
            (scrollPosition * (viewportSize - scrollbarHeight.toPx())).toDp()
        }

        Box(
            modifier = Modifier
                .offset(y = scrollbarOffset)
                .size(width = 8.dp, height = scrollbarHeight)
                .background(
                    color = Color.LightGray.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}

@Composable
fun MediaItemRow(
    item: MediaItem,
    onImageClick: (String) -> Unit,
    onMusicClick: (String) -> Unit,
    onVideoClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = MaterialTheme.shapes.small)
            .clickable {
                when (item.type) {
                    MediaType.IMAGE -> {
                        if (item.filePath != null) {
                            onImageClick(item.filePath)
                        }
                    }
                    MediaType.MUSIC -> {
                        if (item.filePath != null) {
                            onMusicClick(item.filePath)
                        }
                    }
                    else -> {
                        if (item.filePath != null) {
                            onVideoClick(item.filePath)
                        }
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.LightGray, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (item.thumbnail != null) {
                Image(
                    bitmap = item.thumbnail,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = when (item.type) {
                        MediaType.VIDEO -> Icons.Default.PlayArrow
                        MediaType.MUSIC -> Icons.Default.MusicNote
                        MediaType.IMAGE -> Icons.Default.Photo
                    },
                    contentDescription = item.title,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
            item.metadata?.forEach { (key, value) ->
                Text(text = "$key: $value", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}