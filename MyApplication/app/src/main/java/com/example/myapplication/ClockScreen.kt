package com.example.myapplication

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClockApp() {
    var isDarkTheme by remember { mutableStateOf(false) }

    MyApplicationTheme(darkTheme = isDarkTheme) {
        ClockScreen(
            isDarkTheme = isDarkTheme,
            onToggleTheme = { isDarkTheme = !isDarkTheme }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State to manage background image URI
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
                    // Lưu URI vào DataStore
                    BackgroundImageDataStore.saveBackgroundImageUri(context, uri.toString())
                }
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    )
    var isDarkTheme by remember { mutableStateOf(false) }

    // State variables
    var selectedTimeZone by remember { mutableStateOf(TimeZone.getDefault().id) }
    var timeText by remember {
        mutableStateOf(getCurrentTime(is24HourFormat = true, timeZoneId = selectedTimeZone))
    }
    var dateText by remember { mutableStateOf(getCurrentDate()) }
    var is24HourFormat by remember { mutableStateOf(true) }
    var showTimeZoneDialog by remember { mutableStateOf(false) }

    // State to track clock mode
    var clockMode by remember { mutableStateOf(ClockMode.DIGITAL) }

    // Menu expanded state
    var menuExpanded by remember { mutableStateOf(false) }

    // Update time zone name when selectedTimeZone changes
    var selectedTimeZoneName by remember { mutableStateOf("") }

    LaunchedEffect(selectedTimeZone) {
        selectedTimeZoneName = selectedTimeZone.split("/").last().replace("_", " ")
    }

    // Update time and date every second
    LaunchedEffect(is24HourFormat, selectedTimeZone) {
        while (true) {
            timeText = getCurrentTime(is24HourFormat, selectedTimeZone)
            dateText = getCurrentDate()
            delay(1000)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // Display background image
        backgroundImageUri?.let { uri ->
            val painter = rememberImagePainter(data = uri)
            Image(
                painter = painter,
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Crop the image to fit the screen
            )
        }
        Scaffold(
            topBar = {
                // TopAppBar with title, theme toggle, AM/PM toggle, and more options menu
                TopAppBar(
                    title = {
                        Text(
                            text = "Đồng Hồ",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (isDarkTheme) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        // Thêm nút chọn hình nền
                        IconButton(onClick = { pickImageLauncher.launch("image/*") }) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Choose Background Image",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDarkTheme) Color(0xFF121212) else Color.Blue,
                        titleContentColor = if (isDarkTheme) Color.Transparent else Color.Black,
                        navigationIconContentColor = if (isDarkTheme) Color.Transparent else Color.Black,
                        actionIconContentColor = if (isDarkTheme) Color.Transparent else Color.Black
                    )
                )

            },
            containerColor = Color.Transparent ,
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .fillMaxSize() // Fill the available space
                        .padding(bottom = 60.dp) // Move the content up from the bottom (2 rows from the bottom)
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart) // Align to the bottom left of the screen
                            .padding(start = 30.dp) // Adjust the left padding for "Night Mode"
                    ) {
                        // Night Mode Toggle Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = "Night Mode  ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color.Black,
                            )
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = {
                                    isDarkTheme = !isDarkTheme
                                }, // Trực tiếp thay đổi isDarkTheme
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = if (isDarkTheme) Color.Blue else Color.Blue,
                                    uncheckedThumbColor = Color.Gray,
                                    checkedTrackColor = if (isDarkTheme) Color.DarkGray else Color(
                                        0xFFBBDEFB
                                    ),
                                    uncheckedTrackColor = Color(0xFFE0E0E0)
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Align to the bottom right of the screen
                            .padding(end = 20.dp) // Space from the right edge
                    ) {
                        // Floating action button for time zone selection on the right
                        FloatingActionButton(
                            onClick = { showTimeZoneDialog = true },
                            containerColor = if (isDarkTheme) Color.DarkGray else Color.LightGray,
                            shape = CircleShape,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Text(
                                text = "+",
                                color = Color.Blue,
                                fontSize = 35.sp
                            )
                        }
                    }
                }
            },
            content = { paddingValues ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isDarkTheme) Color.Black else Color.Transparent) // Đặt màu nền dựa trên theme
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Display selected time zone name
                    Text(
                        text = selectedTimeZoneName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isDarkTheme) Color.White else Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display clock based on selected mode
                    if (clockMode == ClockMode.ANALOG) {
                        AnalogClock(
                            timeZoneId = selectedTimeZone,
                            is24HourFormat = is24HourFormat,
                            isDarkTheme = isDarkTheme  // Pass the isDarkTheme here
                        )
                    } else {
                        // Digital Clock View
                        Text(
                            text = timeText,
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (isDarkTheme) Color.White else Color.Black,// DAY LA BUG
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Hiện tại: $dateText",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDarkTheme) Color.White else Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Chỉ hiển thị nút 12h/24h khi ở chế độ Digital
                    if (clockMode == ClockMode.DIGITAL) {
                        // Time format toggle (12h / 24h)
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Outer circle with light border
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .border(
                                        2.dp,
                                        color = Color.LightGray,
                                        shape = CircleShape
                                    )
                                    .padding(8.dp)
                            ) {
                                // Inner circle with the time toggle
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .background(
                                            color = Color(0xFF1E88E5),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            is24HourFormat = !is24HourFormat
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (is24HourFormat) "24h" else "12h",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    // Row with two buttons for switching clock modes
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth() // Take up the full width of the screen
                    ) {
                        // Digital Clock Button with increased width and evenly spaced
                        ClockModeButton(
                            icon = ImageVector.vectorResource(id = R.drawable.business_time_solid), // Digital Clock Icon
                            label = "Digital",
                            isSelected = clockMode == ClockMode.DIGITAL,
                            onClick = { clockMode = ClockMode.DIGITAL },
                            modifier = Modifier
                                .weight(1f) // Take equal space
                                .height(50.dp) // Optionally set the height of the button
                        )

                        Spacer(modifier = Modifier.width(16.dp)) // Add space between the buttons

                        // Analog Clock Button with increased width and evenly spaced
                        ClockModeButton(
                            icon = ImageVector.vectorResource(id = R.drawable.clock_regular), // Analog Clock Icon
                            label = "Analog",
                            isSelected = clockMode == ClockMode.ANALOG,
                            onClick = { clockMode = ClockMode.ANALOG },
                            modifier = Modifier
                                .weight(1f) // Take equal space
                                .height(50.dp) // Optionally set the height of the button
                        )
                    }


                    // Bottom space for Night Mode and Floating Action Button to stay at the bottom
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomCenter // Align content to bottom
                    ) {
                        // Floating Action Button and Night Mode button in the same row (already done above)
                    }
                }
                // Display Time Zone Selector Dialog
                if (showTimeZoneDialog) {
                    Dialog(onDismissRequest = { showTimeZoneDialog = false }) {
                        TimeZoneSelectorDialog(
                            selectedTimeZone = selectedTimeZone,
                            onTimeZoneSelected = {
                                selectedTimeZone = it
                                showTimeZoneDialog = false
                            },
                            onDismiss = { showTimeZoneDialog = false },
                            isDarkTheme = isDarkTheme  // Passing the isDarkTheme parameter
                        )
                    }
                }
            }
        )
    }
}

// Reusable Clock Mode Button Composable
@Composable
fun ClockModeButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF1E88E5) else Color(0xFFE0E0E0),
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeZoneSelectorDialog(
    selectedTimeZone: String,
    onTimeZoneSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    var searchText by remember { mutableStateOf("") }
    val availableTimeZones = TimeZone.getAvailableIDs()

    // Chuẩn hóa searchText bằng cách loại bỏ khoảng trắng dư thừa và chia nhỏ thành từng từ
    val normalizedSearchText = searchText.trim().replace(Regex("\\s+"), " ").lowercase(Locale.getDefault())
    val searchKeywords = normalizedSearchText.split(" ") // Chia searchText thành từng từ

    // Lọc các múi giờ dựa trên từ khóa tìm kiếm, không phân biệt hoa thường
    val filteredTimeZones = availableTimeZones.filter { timeZoneId ->
        searchKeywords.all { keyword ->
            timeZoneId.lowercase(Locale.getDefault()).contains(keyword)
        }
    }.sorted()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF121212) else Color.White)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = if (isDarkTheme) Color.White else Color.Black)
            }
            Column {
                Text(
                    "Chọn thành phố",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng cách giữa hai dòng chữ
                Text(
                    "Múi giờ", fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDarkTheme) Color(0xFF424242) else Color(0xFFF0F0F0),
                    RoundedCornerShape(8.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon",
                tint = if (isDarkTheme) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.width(1.dp))
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 13.sp,
                    color = if (isDarkTheme) Color.White else Color.Black
                ),
                placeholder = {
                    Text(
                        "Tìm kiếm khu vực hoặc thành phố",
                        color = if (isDarkTheme) Color.Gray else Color.Black,
                        fontSize = 14.sp
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = if (isDarkTheme) Color.White else Color.Black,
                    focusedIndicatorColor = if (isDarkTheme) Color.White else Color.Black,
                    unfocusedIndicatorColor = if (isDarkTheme) Color.Gray else Color.LightGray
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(filteredTimeZones) { timeZoneId ->
                TimeZoneItem(
                    timeZoneId = timeZoneId,
                    selectedTimeZone = selectedTimeZone,
                    onTimeZoneSelected = onTimeZoneSelected,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}



// Time Zone Item Composable (Existing in your code)
@Composable
fun TimeZoneItem(
    timeZoneId: String,
    selectedTimeZone: String,
    onTimeZoneSelected: (String) -> Unit,
    isDarkTheme: Boolean
) {
    val displayName = timeZoneId.split("/").last().replace("_", " ")
    val country = getCountryFromTimeZone(timeZoneId)
    val offset = getTimeZoneOffset(timeZoneId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onTimeZoneSelected(timeZoneId) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                displayName,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color.Black
            )
            Text("$country $offset", color = if (isDarkTheme) Color.Gray else Color.Black)
        }
        if (timeZoneId == selectedTimeZone) {
            Text("✓", color = if (isDarkTheme) Color.White else Color.Blue)
        }
    }
}

// Enhanced Analog Clock Composable
@Composable
fun AnalogClock(timeZoneId: String, is24HourFormat: Boolean, isDarkTheme: Boolean) {
    val currentTime = remember { mutableStateOf(Calendar.getInstance(TimeZone.getTimeZone(timeZoneId))) }

    // Update the time every second
    LaunchedEffect(timeZoneId) {
        while (true) {
            currentTime.value = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId))
            delay(1000)
        }
    }

    val hour = currentTime.value.get(Calendar.HOUR_OF_DAY) % 12
    val minute = currentTime.value.get(Calendar.MINUTE)
    val second = currentTime.value.get(Calendar.SECOND)

    Box(
        modifier = Modifier
            .size(250.dp) // Smaller size for better aesthetics
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = size.minDimension / 2

            // Draw the clock face with a conditional background color
            drawCircle(
                color = Color(0xFFADD8E6), // Mã HEX cho màu xanh dương nhạt
                radius = radius,
                center = center // Đặt tâm vòng tròn ở giữa Canvas
            )

            // Draw hour numbers and dots with conditional colors
            val numberRadius = radius * 0.65f  // Giảm bán kính của số
            val dotRadius = radius * 0.85f     // Đặt dotRadius lớn hơn để đẩy dấu chấm ra xa hơn

            for (i in 1..12) {
                val angle = Math.toRadians((i * 30 - 90).toDouble())

                // Calculate positions for hour numbers
                val numberX = (canvasWidth / 2 + numberRadius * cos(angle)).toFloat()
                val numberY = (canvasHeight / 2 + numberRadius * sin(angle)).toFloat()

                // Draw dot next to each hour number
                val dotX = (canvasWidth / 2 + dotRadius * cos(angle)).toFloat()
                val dotY = (canvasHeight / 2 + dotRadius * sin(angle)).toFloat()

                // Draw dot with color condition
                drawCircle(
                    color = if (isDarkTheme) Color.White else Color.Black,
                    radius = 8f,
                    center = Offset(dotX, dotY)
                )

                // Draw hour number with adjusted position
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 28.sp.toPx()
                        isAntiAlias = true
                    }
                    drawText(
                        i.toString(),
                        numberX,
                        numberY + 12.dp.toPx(),  // Adjust for better positioning (centered)
                        paint
                    )
                }
            }

            // Draw minute and hour hands with conditional colors
            val hourAngle = (hour + minute / 60f) * 30f - 90f
            val minuteAngle = (minute + second / 60f) * 6f - 90f
            val secondAngle = second * 6f - 90f

            drawLine(
                color = if (isDarkTheme) Color.LightGray else Color(0xFF616161),
                start = Offset(canvasWidth / 2, canvasHeight / 2),
                end = Offset(
                    (canvasWidth / 2 + radius * 0.5f * cos(Math.toRadians(hourAngle.toDouble()))).toFloat(),
                    (canvasHeight / 2 + radius * 0.5f * sin(Math.toRadians(hourAngle.toDouble()))).toFloat()
                ),
                strokeWidth = 10f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = if (isDarkTheme) Color.LightGray else Color(0xFF424242),
                start = Offset(canvasWidth / 2, canvasHeight / 2),
                end = Offset(
                    (canvasWidth / 2 + radius * 0.7f * cos(Math.toRadians(minuteAngle.toDouble()))).toFloat(),
                    (canvasHeight / 2 + radius * 0.7f * sin(Math.toRadians(minuteAngle.toDouble()))).toFloat()
                ),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )

            // Draw second hand in red
            drawLine(
                color = Color.Red,
                start = Offset(canvasWidth / 2, canvasHeight / 2),
                end = Offset(
                    (canvasWidth / 2 + radius * 0.9f * cos(Math.toRadians(secondAngle.toDouble()))).toFloat(),
                    (canvasHeight / 2 + radius * 0.9f * sin(Math.toRadians(secondAngle.toDouble()))).toFloat()
                ),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )

            // Draw the center circle with a subtle shadow for a 3D effect
            drawCircle(
                color = Color.Black,
                radius = 8f
            )
        }

    }
}
// Helper function to get country from time zone
private fun getCountryFromTimeZone(timeZoneId: String): String {
    return timeZoneId.split("/").getOrNull(0)?.replace("_", " ") ?: ""
}

// Helper function to get time zone offset
private fun getTimeZoneOffset(timeZoneId: String): String {
    val timeZone = TimeZone.getTimeZone(timeZoneId)
    val hours = timeZone.rawOffset / (1000 * 60 * 60)
    val minutes = Math.abs((timeZone.rawOffset / (1000 * 60)) % 60)
    val sign = if (hours >= 0) "+" else "-"
    return "GMT$sign${"%02d".format(hours)}:${"%02d".format(minutes)}"
}
// Helper function to get current time as string
private fun getCurrentTime(is24HourFormat: Boolean, timeZoneId: String): String {
    val timeFormat = if (is24HourFormat) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    } else {
        SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    }
    timeFormat.timeZone = TimeZone.getTimeZone(timeZoneId)
    return timeFormat.format(Calendar.getInstance().time)
}
// Helper function to get current date as string
private fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(Calendar.getInstance().time)
}