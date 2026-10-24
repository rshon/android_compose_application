package com.example.ch_basic_image

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.ch_basic_image.ui.theme.Ch_basic_ImageTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ImageInfo(val uri: Uri, val name: String, val dateAdded: Long)

enum class SortOrder { Ascending, Descending }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ch_basic_ImageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        GalleryScreen()
                    }
                }
            }
        }
    }
}

fun loadImagesFromMediaStore(context: Context): List<ImageInfo> {
    val imageList = mutableListOf<ImageInfo>()
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED, // added date
        MediaStore.Images.Media.DATA        // file path
    )

    val sortOrderQuery = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    try {
        context.contentResolver.query(
            collection,
            projection,
            null, // selection
            null, // selectionArgs
            sortOrderQuery
        )?.use { cursor -> // ensures automatic close with use{} block
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn) ?: "Unknown"
                val dateAdded = cursor.getLong(dateAddedColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imageList.add(ImageInfo(contentUri, name, dateAdded))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return imageList
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen() {
    val context = LocalContext.current

    val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        android.Manifest.permission.READ_MEDIA_IMAGES
    else
        android.Manifest.permission.READ_EXTERNAL_STORAGE

    var hasPermission by remember { mutableStateOf<Boolean?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasPermission = it
    }
    LaunchedEffect(Unit) {
        val permissionCheckResult = context.checkSelfPermission(readPermission) == android.content.pm.PackageManager.PERMISSION_GRANTED
        hasPermission = permissionCheckResult
        if (!permissionCheckResult) {
            launcher.launch(readPermission)
        }
    }

    val imageInfos by produceState(initialValue = emptyList<ImageInfo>(), hasPermission) {
        if (hasPermission == true) {
            value = withContext(Dispatchers.IO) {
                loadImagesFromMediaStore(context)
            }
        }
    }

    val sortOptions = listOf("날짜순", "이름순")
    var sortOption by remember { mutableStateOf(sortOptions[0]) }
    var sortOrder by remember { mutableStateOf(SortOrder.Descending) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val sortedImages = remember(imageInfos, sortOption, sortOrder) {
        val comparator = when (sortOption) {
            "이름순" -> compareBy<ImageInfo> { it.name }
            "날짜순" -> compareBy<ImageInfo> { it.dateAdded }
            else -> compareBy { it.dateAdded }
        }
        val sortedList = imageInfos.sortedWith(comparator)
        if (sortOrder == SortOrder.Descending) {
            sortedList.reversed()
        } else {
            sortedList
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ComboBox (Dropdown Menu)
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    value = sortOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("정렬 기준") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .width(150.dp)
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    sortOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                sortOption = selectionOption
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Radio Buttons
            Row {
                SortOrder.values().forEach { order ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { sortOrder = order }
                            .padding(start = 8.dp)
                    ) {
                        RadioButton(
                            selected = (sortOrder == order),
                            onClick = { sortOrder = order }
                        )
                        Text(
                            text = if (order == SortOrder.Ascending) "오름차순" else "내림차순",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        when (hasPermission) {
            null -> Spacer(modifier = Modifier.height(1.dp))
            false -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "이미지를 보려면 저장소 읽기 권한이 필요합니다.",
                    modifier = Modifier.padding(16.dp)
                )
            }
            true -> {
                if (sortedImages.isEmpty() && imageInfos.isNotEmpty()) {
                    // while loading (produceState has not been yet completed)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (sortedImages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "표시할 이미지가 없습니다.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    ImageGridWithFullscreenDialog(
                        sortedImages = sortedImages,
                        selectedUri = selectedUri,
                        onUriSelected = { uri -> selectedUri = uri },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ImageGridWithFullscreenDialog(
    sortedImages: List<ImageInfo>,
    selectedUri: Uri?,
    onUriSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = sortedImages, key = { it.uri }) { imageInfo ->
                Image(
                    painter = rememberAsyncImagePainter(model = imageInfo.uri),
                    contentDescription = imageInfo.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .aspectRatio(1f)
                        .border(0.5.dp, Color.LightGray)
                        .clickable { onUriSelected(imageInfo.uri) }
                )
            }
        }

        selectedUri?.let { uri ->
            Dialog(
                onDismissRequest = { onUriSelected(null) },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .clickable { onUriSelected(null) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = "선택된 이미지 전체화면",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GalleryScreenPreview() {
    Ch_basic_ImageTheme { // 테마 적용
        GalleryScreen()
    }
}
