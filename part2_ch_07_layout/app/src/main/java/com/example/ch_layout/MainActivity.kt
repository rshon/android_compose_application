package com.example.ch_layout

import android.content.ContentUris
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.ch_layout.ui.theme.Ch_layoutTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import coil.compose.AsyncImage
import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Divider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.constraintlayout.compose.Dimension


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ch_layoutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                    ColumnExample()
//                    RowExample()
                    BoxExample()
//                    BoxLoadingDemo()
//                    ConstraintLayoutExample()
//                    ConstraintLayoutChainsExample()
//                    ConstraintLayoutGuidelineExample()
//                    ConstraintLayoutMarginExample()
//                    ConstraintLayoutBarrierBoxExample()
//                    LazyListWithStateExample()
//                    LazyRowExample()
//                    Mp3ThumbnailScreen()
//                    ListKeyStateExample()
                }
            }
        }
    }
}

data class ItemData(val id: Int, val name: String)

@SuppressLint("RememberReturnType")
@Composable
fun ListKeyStateExample() {
    Scaffold(
        content = { paddingValues ->
            val items = remember {
                mutableStateListOf(
                    ItemData(1, "A"),
                    ItemData(2, "B"),
                    ItemData(3, "C")
                )
            }

            var nextId by remember { mutableIntStateOf(4) }
            var nextChar by remember { mutableStateOf('D') }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Button(onClick = {
                    items.add(0, ItemData(nextId++, nextChar.toString()))
                    Log.d("ListKeyStateExample", "Added item: $items")
                    nextChar++
                }) {
                    Text("add new item at front")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Without key", style = MaterialTheme.typography.titleMedium)
                        LazyColumn(
                            modifier = Modifier
                                .wrapContentSize()
                                .border(2.dp, Color.Magenta)
                        ) {
                            items(
                                items = items,
                            ) { item ->
                                ListItemContent(item = item, listId = "NoKey")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(7.dp))
                    VerticalDivider()
                    Spacer(modifier = Modifier.width(7.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("With key (item.id)", style = MaterialTheme.typography.titleMedium)
                        LazyColumn(
                            modifier = Modifier
                                .wrapContentSize()
                                .border(2.dp, Color.Green)
                        ) {
                            items(
                                items = items,
                                key = { item -> item.id }
                            ) { item ->
                                ListItemContent(item = item, listId = "WithKey")
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ListItemContent(item: ItemData, listId: String) {
    var clickCount by remember { mutableIntStateOf(0) }

    // to see which item is in involved in recomposition (called when item.id(key) is updated)
    LaunchedEffect(item.id) {
        Log.d("ListKeyStateExample", "[$listId] Composing item: ${item.name} (id=${item.id}), initial count: $clickCount")
    }

    LaunchedEffect(clickCount) {
        if (clickCount > 0) {
            Log.d("ListKeyStateExample", "[$listId] Recomposing item: ${item.name} (id=${item.id}) due to: $clickCount")
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable {
                clickCount++
                Log.d(
                    "ListKeyStateExample",
                    "[$listId] Clicked item: ${item.name}, count: $clickCount"
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "ID: ${item.id}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(50.dp)
            )
            Text(
                "Name: ${item.name}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text("Count: $clickCount", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }

    HorizontalDivider()
}

@Composable
fun Mp3ThumbnailScreen() {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    // ready for ActivityResultLauncher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    // check permission at the first execution
    LaunchedEffect(Unit) {
        val neededPerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(context, neededPerm)
            != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(neededPerm)
        } else {
            hasPermission = true
        }
    }

    if (hasPermission) {
        DownloadMp3ThumbnailRow()
    } else {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Required audio p ermisson!")
        }
    }
}

@Composable
fun DownloadMp3ThumbnailRow() {
    val context = LocalContext.current
    var albumArtUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // async loading with LaunchedEffect
    LaunchedEffect(context) {
        val list = mutableListOf<Uri>()
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA
        )
        val selection = "${MediaStore.Audio.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%/Download/%")
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, selection, selectionArgs, sortOrder
        )?.use { cursor ->
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            while (cursor.moveToNext()) {
                val albumId = cursor.getLong(albumCol)
                list += ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
            }
        }
        albumArtUris = list
    }

    Scaffold(
        modifier = Modifier,
        content = { paddingValues ->
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
//                    .height(100.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(albumArtUris, key = { it.toString() }) { artUri ->
                        Card(
                            modifier = Modifier.size(100.dp),
                            shape = CardDefaults.elevatedShape,
                            colors = CardDefaults.cardColors(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            AsyncImage(
                                model = artUri,
                                contentDescription = "album art",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(albumArtUris, key = { it.toString() }) { artUri ->
                        Card(
                            modifier = Modifier.size(100.dp),
                            shape = CardDefaults.elevatedShape,
                            colors = CardDefaults.cardColors(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            AsyncImage(
                                model = artUri,
                                contentDescription = "album art",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    )
}


@Composable
fun LazyRowExample() {
    Scaffold(
        modifier = Modifier,
        content = { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val (row1, row2, row3) = createRefs()

                CardsWithLazyRow(
                    modifier = Modifier
                        .constrainAs(row1) {
                            top.linkTo(parent.top, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .height(120.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                CardsWithLazyRow(
                    modifier = Modifier
                        .constrainAs(row2) {
                            top.linkTo(row1.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .height(120.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                CardsWithLazyRow(
                    modifier = Modifier
                        .constrainAs(row3) {
                            top.linkTo(row2.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .height(120.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    )
}

@Composable
fun CardsWithLazyRow(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(100) { index ->
            val number = index + 1
            val randomColor = Color(
                red = Random.nextInt(256),
                green = Random.nextInt(256),
                blue = Random.nextInt(256),
                alpha = 255
            )

            val luminance = (0.299 * randomColor.red + 0.587 * randomColor.green + 0.114 * randomColor.blue) / 255
            val textColor = if (luminance > 0.5) Color.Black else Color.White

            Card(
                modifier = Modifier
                    .size(100.dp),
                shape = CardDefaults.elevatedShape,
                colors = CardDefaults.cardColors(
                    containerColor = randomColor
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
fun LazyListWithStateExample() {
    Scaffold(
        modifier = Modifier,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(
                        items = (1..100).toList(),
                        key = { it }
                    ) { number ->
                        Text(
                            text = "Item #$number",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                LaunchedEffect(listState.firstVisibleItemIndex) {
                    println("the index of the 1st item: ${listState.firstVisibleItemIndex}")
                }

                val coroutineScope = rememberCoroutineScope()
                Button(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index = 0)
                        }
                    },
                    modifier = Modifier
                        .padding(25.dp)
                ) {
                    Text("Go to the top")
                }
            }
        }
    )
}

@Composable
fun ConstraintLayoutBarrierBoxExample() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val (box1, box2, box3) = createRefs()
                val barrier = createEndBarrier(box1, box2)

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Green.copy(alpha = 0.5f))
                        .border(width = 1.dp, color = Color.Black)
                        .constrainAs(box1) {
                            top.linkTo(parent.top, margin = 20.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                        }
                )
                Box(
                    modifier = Modifier
                        .size(50.dp, 40.dp)
                        .background(Color.Yellow.copy(alpha = 0.5f))
                        .border(width = 1.dp, color = Color.Black)
                        .constrainAs(box2) {
                            top.linkTo(box1.bottom, margin = 8.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                        }
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Red.copy(alpha = 0.5f))
                        .border(width = 1.dp, color = Color.Black)
                        .constrainAs(box3) {
                            top.linkTo(parent.top, margin = 20.dp)
                            start.linkTo(barrier, margin = 16.dp)
                        }
                )
            }
        }
    )
}

@Composable
fun ConstraintLayoutMarginExample() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val (box1, box2) = createRefs()

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Green.copy(alpha = 0.5f))
                        .constrainAs(box1) {
                            top.linkTo(parent.top, margin = 20.dp)
                            start.linkTo(parent.start, margin = 32.dp)
                        }
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Yellow.copy(alpha = 0.5f))
                        .constrainAs(box2) {
                            top.linkTo(parent.top, margin = 20.dp)
                            end.linkTo(parent.end, margin = 32.dp)
                        }
                )
            }
        }
    )
}

@Composable
fun ConstraintLayoutGuidelineExample() {
    Scaffold(
        modifier = Modifier,
        content = { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val guideline = createGuidelineFromTop(0.5f)
                val (box1, box2, box3) = createRefs()

                createHorizontalChain(box1, box2, box3, chainStyle = ChainStyle.Spread)

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Green.copy(alpha = 0.5f))
                        .constrainAs(box1) {
                            top.linkTo(guideline, margin = 20.dp)
                        }
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Yellow.copy(alpha = 0.5f))
                        .constrainAs(box2) {
                            top.linkTo(guideline, margin = 20.dp)
                        }
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Red.copy(alpha = 0.5f))
                        .constrainAs(box3) {
                            top.linkTo(guideline, margin = 20.dp)
                        }
                )
            }
        }
    )
}

@Composable
fun ConstraintLayoutChainsExample() {
    Scaffold(
        modifier = Modifier,
        content = { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val (box1, box2, box3) = createRefs()

                createHorizontalChain(box1, box2, box3, chainStyle = ChainStyle.SpreadInside)

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Green.copy(alpha = 0.5f))
                        .constrainAs(box1) {
                            top.linkTo(parent.top, margin = 20.dp)
                        }
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Yellow.copy(alpha = 0.5f))
                        .constrainAs(box2) {
                            top.linkTo(parent.top, margin = 20.dp)
                        }
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Red.copy(alpha = 0.5f))
                        .constrainAs(box3) {
                            top.linkTo(parent.top, margin = 20.dp)
                        }
                )
            }
        }
    )
}

@Composable
fun ConstraintLayoutExample() {
    Scaffold(
        modifier = Modifier,
        content = { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val (textRef, buttonRef) = createRefs()

                Text(
                    text = "Text composable",
                    modifier = Modifier
                        .background(Color.Yellow.copy(alpha = 0.5f))
                        .constrainAs(textRef) {
                            top.linkTo(parent.top, margin = 16.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                        }
                )

                Button(
                    onClick = { /* EMPTY */ },
                    modifier = Modifier.constrainAs(buttonRef) {
                        top.linkTo(textRef.bottom, margin = 8.dp)
                        start.linkTo(textRef.end, margin = 8.dp)
                    }
                ) {
                    Text("Button composable")
                }
            }
        }
    )
}

@Composable
fun BoxLoadingDemo() {
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Content", style = MaterialTheme.typography.titleLarge, fontSize = 30.sp)
            Button(onClick = { isLoading = true }) {
                Text("Start", fontSize = 20.sp)
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 4.dp
                )
            }

            LaunchedEffect(key1 = Unit) {
                delay(2_000)
                isLoading = false
            }
        }
    }
}

@Composable
fun BoxExample() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .heightIn(min = 50.dp),
                    propagateMinConstraints = false
                ) {
                    Text(
                        "minHeight = 0",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .background(Color.Green)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .heightIn(min = 50.dp),
                    propagateMinConstraints = true
                ) {
                    Text(
                        "minHeight = 50.dp",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .background(Color.Green)
                    )
                }
            }
        }
    )
}


@Composable
fun RowExample() {
    var fondSize by remember { mutableStateOf(15.sp) }
    var enlargeButtonText: String = "Enlarge font size"
    var reduceButtonText: String = "Reduce font size"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "1st text", fontSize = fondSize)
                    VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(text = "2nd text", fontSize = fondSize)
                    VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(text = "3rd text", fontSize = fondSize)
                    VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Button(
                        onClick = { fondSize = if (fondSize == 15.sp) 25.sp else 15.sp },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (fondSize == 15.sp) {
                            Text(enlargeButtonText)
                        } else {
                            Text(reduceButtonText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    )
}


@Composable
fun ColumnExample() {
    var fondSize by remember { mutableStateOf(20.sp) }
    var enlargeButtonText by remember { mutableStateOf("Enlarge font size") }
    var reduceButtonText by remember { mutableStateOf("Reduce font size") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "1st text", fontSize = fondSize)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(text = "2nd text", fontSize = fondSize)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(text = "3rd text", fontSize = fondSize)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Button(onClick = { fondSize = if (fondSize == 20.sp) 40.sp else 20.sp }) {
                    if (fondSize == 20.sp) {
                        Text(enlargeButtonText)
                    } else {
                        Text(reduceButtonText)
                    }
                }
            }
        }
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Ch_layoutTheme {
        Greeting("Android")
    }
}