package com.invictus.attendanceapp.feature.staff.presentation.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.invictus.attendanceapp.core.ui.InAppMapView
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffProfileScreen(
    viewModel: StaffProfileViewModel,
    onBackClick: () -> Unit,
    onEnrollFaceClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // State for Full Screen Photo Viewer
    var fullScreenPhotoData by remember { mutableStateOf<Pair<Any, String>?>(null) }

    // State for Attendance Coordinate & Detail Dialog
    var selectedAttendanceDetails by remember { mutableStateOf<Attendance?>(null) }

    // Full Screen Photo Dialog
    fullScreenPhotoData?.let { (photoModel, photoTitle) ->
        Dialog(
            onDismissRequest = { fullScreenPhotoData = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
            ) {
                // Top Bar with Title and Close Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = photoTitle,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { fullScreenPhotoData = null }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Centered Image
                AsyncImage(
                    model = photoModel,
                    contentDescription = photoTitle,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    // Attendance In-App Map & Details Dialog
    selectedAttendanceDetails?.let { attendance ->
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val dateStr = dateFormat.format(Date(attendance.timestamp))
        val timeStr = timeFormat.format(Date(attendance.timestamp))

        val selfieModel: Any? = when {
            attendance.selfiePath.isBlank() -> null
            attendance.selfiePath.startsWith("http://") || attendance.selfiePath.startsWith("https://") -> attendance.selfiePath
            File(attendance.selfiePath).exists() -> File(attendance.selfiePath)
            else -> null
        }

        AlertDialog(
            onDismissRequest = { selectedAttendanceDetails = null },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Attendance Location", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    IconButton(onClick = { selectedAttendanceDetails = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Interactive In-App Map View
                    InAppMapView(
                        latitude = attendance.latitude,
                        longitude = attendance.longitude,
                        label = "$dateStr • $timeStr",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selfieModel != null) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        fullScreenPhotoData = selfieModel to "$dateStr • $timeStr Selfie"
                                    }
                            ) {
                                AsyncImage(
                                    model = selfieModel,
                                    contentDescription = "Selfie",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(2.dp),
                                    color = Color.Black.copy(alpha = 0.6f),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = "Zoom",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(3.dp)
                                            .size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Face Verified ✓",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "$dateStr • $timeStr",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Zoomed Coordinate Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "EXACT GPS COORDINATES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Latitude",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = String.format(Locale.US, "%.6f°", attendance.latitude),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Longitude",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = String.format(Locale.US, "%.6f°", attendance.longitude),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            val mapUri = Uri.parse("geo:${attendance.latitude},${attendance.longitude}?q=${attendance.latitude},${attendance.longitude}(Attendance+Check-in)")
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                            context.startActivity(mapIntent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open in Google Maps", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val staff = uiState.staff
            if (staff == null) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Staff member not found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val imageSource = staff.faceImageUrl ?: staff.faceImagePath
                                val profileImageModel: Any? = when {
                                    imageSource.isNullOrBlank() -> null
                                    imageSource.startsWith("http://") || imageSource.startsWith("https://") -> imageSource
                                    File(imageSource).exists() -> File(imageSource)
                                    else -> null
                                }

                                if (profileImageModel != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(96.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                fullScreenPhotoData = profileImageModel to "${staff.name} - Enrolled Biometric"
                                            }
                                    ) {
                                        AsyncImage(
                                            model = profileImageModel,
                                            contentDescription = "Enrollment Image",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                } else {
                                    Surface(
                                        modifier = Modifier
                                            .size(96.dp)
                                            .clip(CircleShape),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(48.dp),
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = staff.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Employee ID: ${staff.employeeId}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = { onEnrollFaceClick(staff.id) }
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (staff.isFaceEnrolled) "Re-enroll Face" else "Enroll Face")
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Attendance History (${uiState.attendanceHistory.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (uiState.attendanceHistory.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No attendance records found.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.attendanceHistory, key = { it.id }) { attendance ->
                            AttendanceHistoryCard(
                                attendance = attendance,
                                onPhotoClick = { model, title ->
                                    fullScreenPhotoData = model to title
                                },
                                onCardClick = {
                                    selectedAttendanceDetails = attendance
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceHistoryCard(
    attendance: Attendance,
    onPhotoClick: (Any, String) -> Unit,
    onCardClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(attendance.timestamp))
    val timeStr = timeFormat.format(Date(attendance.timestamp))

    val selfieModel: Any? = when {
        attendance.selfiePath.isBlank() -> null
        attendance.selfiePath.startsWith("http://") || attendance.selfiePath.startsWith("https://") -> attendance.selfiePath
        File(attendance.selfiePath).exists() -> File(attendance.selfiePath)
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selfieModel != null) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPhotoClick(selfieModel, "$dateStr • $timeStr Selfie") }
                ) {
                    AsyncImage(
                        model = selfieModel,
                        contentDescription = "Selfie",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Surface(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$dateStr • $timeStr",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Lat: ${String.format(Locale.US, "%.5f", attendance.latitude)}, Long: ${String.format(Locale.US, "%.5f", attendance.longitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
