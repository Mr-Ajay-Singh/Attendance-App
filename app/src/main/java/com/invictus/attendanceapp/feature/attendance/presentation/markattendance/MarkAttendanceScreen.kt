package com.invictus.attendanceapp.feature.attendance.presentation.markattendance

import android.Manifest
import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.invictus.attendanceapp.core.camera.CameraPreview
import com.invictus.attendanceapp.core.camera.takePictureBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MarkAttendanceScreen(
    viewModel: MarkAttendanceViewModel,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isFaceDetected by remember { mutableStateOf(false) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Reset captured bitmap if processing finished with error
    LaunchedEffect(uiState.isProcessing, uiState.error) {
        if (!uiState.isProcessing && uiState.error != null) {
            capturedBitmap = null
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = { Text("Confirm Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out from this account?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout { onLogoutClick() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Success Dialog
    uiState.recordedAttendance?.let { attendance ->
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        AlertDialog(
            onDismissRequest = {
                capturedBitmap = null
                viewModel.dismissSuccessDialog()
            },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Attendance Recorded!", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val selfieFile = File(attendance.selfiePath)
                    if (selfieFile.exists()) {
                        AsyncImage(
                            model = selfieFile,
                            contentDescription = "Attendance Selfie",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Text(
                        text = "Face Verified Successfully ✓",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Date: ${dateFormat.format(Date(attendance.timestamp))}")
                    Text(text = "Time: ${timeFormat.format(Date(attendance.timestamp))}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Location: Lat ${String.format(Locale.US, "%.5f", attendance.latitude)}, Long ${String.format(Locale.US, "%.5f", attendance.longitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    capturedBitmap = null
                    viewModel.dismissSuccessDialog()
                }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Attendance", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val staff = uiState.staff

            if (uiState.isLoadingStaff && staff == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (staff != null && !staff.faceEnrolled) {
                // Biometric Registration Pending / Required Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Biometric Not Registered",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${staff.name} (${staff.employeeId})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Please request your panel to register your biometric first, then only you can be eligible for attendance.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.refreshStaffStatus() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !uiState.isLoadingStaff
                    ) {
                        if (uiState.isLoadingStaff) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Check Registration Status", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Regular Mark Attendance Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Good day!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    staff?.let { s ->
                        Text(
                            text = "${s.name} (${s.employeeId})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (permissionsState.allPermissionsGranted) {
                                isFaceDetected = false
                                capturedBitmap = null
                                viewModel.openCamera()
                            } else {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Mark Attendance", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Camera Verification Overlay Dialog
                if (uiState.isCameraOpen) {
                    val frameBorderColor by animateColorAsState(
                        targetValue = if (isFaceDetected) Color(0xFF43A047) else Color(0xFFE53935),
                        animationSpec = tween(300),
                        label = "frameBorderColor"
                    )

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // If processing, shut down camera completely and display frozen captured selfie
                            if (uiState.isProcessing && capturedBitmap != null) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        bitmap = capturedBitmap!!.asImageBitmap(),
                                        contentDescription = "Captured Selfie",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.6f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(56.dp),
                                                color = Color.White,
                                                strokeWidth = 4.dp
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = "Verifying biometric with server...",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Live Camera Preview
                                CameraPreview(
                                    modifier = Modifier.fillMaxSize(),
                                    isProcessing = false,
                                    onFaceDetectionChanged = { detected ->
                                        isFaceDetected = detected
                                    },
                                    onImageCaptureCreated = { imageCapture = it }
                                )

                                // Face frame oval with dynamic Green/Red border
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(280.dp)
                                                .clip(CircleShape)
                                                .border(BorderStroke(4.dp, frameBorderColor), CircleShape)
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Face Detection Status Pill
                                        Surface(
                                            color = if (isFaceDetected) Color(0xFF43A047).copy(alpha = 0.85f) else Color(0xFFE53935).copy(alpha = 0.85f),
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = if (isFaceDetected) "Face Detected ✓" else "Position Face in Frame",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        capturedBitmap = null
                                        viewModel.closeCamera()
                                    },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                // Bottom Action Drawer
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Center face inside frame & capture",
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        if (uiState.error != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = uiState.error!!,
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = {
                                                imageCapture?.let { capture ->
                                                    coroutineScope.launch {
                                                        try {
                                                            val bitmap = capture.takePictureBitmap(context)
                                                            capturedBitmap = bitmap
                                                            viewModel.processSelfieAndMarkAttendance(bitmap)
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            enabled = imageCapture != null
                                        ) {
                                            Text("Verify Face & Record Attendance", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
