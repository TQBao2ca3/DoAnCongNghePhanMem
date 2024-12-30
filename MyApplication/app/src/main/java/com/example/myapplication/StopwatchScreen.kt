import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchScreen(
    modifier: Modifier = Modifier,
    viewModel: StopwatchViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val timeMillis by viewModel.timeMillis.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val laps by viewModel.laps.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var backgroundImageUri by remember { mutableStateOf<Uri?>(null) }

    // Load URI from DataStore
    LaunchedEffect(Unit) {
        BackgroundImageDataStore.getBackgroundImageUri(context).collect { savedUri ->
            savedUri?.let {
                backgroundImageUri = Uri.parse(it)
            }
        }
    }

    // Launch the file picker
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                backgroundImageUri = uri
                coroutineScope.launch {
                    BackgroundImageDataStore.saveBackgroundImageUri(context, uri.toString())
                }
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Display background image
        backgroundImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("Bấm giờ", fontSize = 26.sp, fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = {
                            pickImageLauncher.launch("image/*")
                        }) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Choose Background Image",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Blue,
                        titleContentColor = Color.Black
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Đồng hồ
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold)) {
                            append(
                                "%02d:%02d:%02d.".format(
                                    (timeMillis / 3600000) % 24,
                                    (timeMillis / 60000) % 60,
                                    (timeMillis / 1000) % 60
                                )
                            )
                        }
                        withStyle(style = SpanStyle(fontSize = 24.sp, color = Color.Gray)) {
                            append("%03d".format(timeMillis % 1000))
                        }
                    },
                    color = Color.Black,
                    modifier = Modifier.padding(top = 32.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
                ControlsRow(
                    isRunning,
                    { viewModel.toggleRunning() },
                    { viewModel.reset() },
                    { viewModel.addLap(); scrollToBottom(listState, coroutineScope) }
                )

                Spacer(modifier = Modifier.height(24.dp))
                if (laps.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Button(
                            onClick = { viewModel.clearLaps() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Clear All Laps")
                            Spacer(Modifier.width(12.dp))
                            Text(text = "Clear All Laps", color = Color.White, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(
                                    color = Color(0xFFADD8E6),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            itemsIndexed(laps) { index, lap ->
                                Text(
                                    "Lap ${index + 1}: ${"%02d:%02d:%02d.%03d".format(
                                        (lap / 3600000) % 24,
                                        (lap / 60000) % 60,
                                        (lap / 1000) % 60,
                                        lap % 1000
                                    )}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 20.dp, top = 9.dp, bottom = 7.dp),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ControlsRow(isRunning: Boolean, toggleRun: () -> Unit, resetLaps: () -> Unit, addLap: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = toggleRun,
            colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) Color.Red else Color.Green)
        ) {
            Icon(if (isRunning) Icons.Filled.Stop else Icons.Filled.PlayArrow, contentDescription = if (isRunning) "Stop" else "Start")
            Spacer(Modifier.width(8.dp))
            Text(text = if (isRunning) "Stop" else "Start", color = Color.White, fontSize = 18.sp)
        }
        Button(
            onClick = resetLaps,
            enabled = !isRunning,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                disabledContainerColor = Color(0xFF90A4AE) // Màu xanh dương nhạt khi bị disabled
            )
        ) {
            Icon(Icons.Filled.Loop, contentDescription = "Reset")
            Spacer(Modifier.width(8.dp))
            Text(text = "Reset", color = if (!isRunning) Color.White else Color.DarkGray, fontSize = 18.sp)
        }
        Button(
            onClick = addLap,
            enabled = isRunning,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                disabledContainerColor = Color(0xFF90A4AE) // Màu xanh dương nhạt khi bị disabled
            )
        ) {
            Icon(Icons.Filled.ClearAll, contentDescription = "Lap")
            Spacer(Modifier.width(8.dp))
            Text(text = "Lap", color = if (isRunning) Color.White else Color.DarkGray, fontSize = 18.sp)
        }
    }
}


fun scrollToBottom(listState: LazyListState, coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        listState.animateScrollToItem(index = Int.MAX_VALUE)
    }
}
