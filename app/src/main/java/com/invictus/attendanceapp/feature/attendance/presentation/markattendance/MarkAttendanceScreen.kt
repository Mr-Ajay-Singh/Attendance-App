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
import androidx.compose.ui.text.font.FontFamily
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
import com.invictus.attendanceapp.ui.theme.KinpakuGold
import com.invictus.attendanceapp.ui.theme.PatinaTeal
import com.invictus.attendanceapp.ui.theme.VermilionRed
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
                    tint = VermilionRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Confirm Logout",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out from your staff portal session?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout { onLogoutClick() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VermilionRed),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Attendance Confirmation Dialog
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
                    tint = PatinaTeal,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Attendance Recorded!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val selfieFile = File(attendance.selfiePath)
                    if (selfieFile.exists()) {
                        AsyncImage(
                            model = selfieFile,
                            contentDescription = "Attendance Selfie",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Surface(
                        color = PatinaTeal.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, PatinaTeal.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Face Verified Successfully ✓",
                            color = PatinaTeal,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Date: ${dateFormat.format(Date(attendance.timestamp))}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Time: ${timeFormat.format(Date(attendance.timestamp))}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lat: ${String.format(Locale.US, "%.5f", attendance.latitude)} • Long: ${String.format(Locale.US, "%.5f", attendance.longitude)}",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        capturedBitmap = null
                        viewModel.dismissSuccessDialog()
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "STAFF ATTENDANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = VermilionRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (staff != null && !staff.faceEnrolled) {
                // Biometric Registration Required State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        color = VermilionRed.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, VermilionRed.copy(alpha = 0.3f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = VermilionRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Biometric Registration Required",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${staff.name} (${staff.employeeId})",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = VermilionRed.copy(alpha = 0.08f)
                        ),
                        border = BorderStroke(1.dp, VermilionRed.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = VermilionRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Please request your administrator to register your face biometric first before marking attendance.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { viewModel.refreshStaffStatus() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(4.dp),
                        enabled = !uiState.isLoadingStaff,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
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
                            .size(96.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Good day",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Thin
                    )

                    staff?.let { s ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${s.name} • ${s.employeeId}",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

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
                            .height(52.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KinpakuGold,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Mark Attendance", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Camera Verification Overlay Dialog
                if (uiState.isCameraOpen) {
                    val frameBorderColor by animateColorAsState(
                        targetValue = if (isFaceDetected) PatinaTeal else VermilionRed,
                        animationSpec = tween(300),
                        label = "frameBorderColor"
                    )

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
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
                                            .background(Color.Black.copy(alpha = 0.7f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(56.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                strokeWidth = 3.dp
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = "VERIFYING BIOMETRIC EMBEDDING...",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            } else {
                                CameraPreview(
                                    modifier = Modifier.fillMaxSize(),
                                    isProcessing = false,
                                    onFaceDetectionChanged = { detected ->
                                        isFaceDetected = detected
                                    },
                                    onImageCaptureCreated = { imageCapture = it }
                                )

                                // Face Frame Dynamic Overlay
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
                                                .border(BorderStroke(3.dp, frameBorderColor), CircleShape)
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Status Pill
                                        Surface(
                                            color = if (isFaceDetected) PatinaTeal.copy(alpha = 0.9f) else VermilionRed.copy(alpha = 0.9f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (isFaceDetected) "FACE DETECTED ✓" else "POSITION FACE IN FRAME",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelSmall,
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
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Center face in frame & capture",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )

                                        if (uiState.error != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                color = VermilionRed.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(1.dp, VermilionRed.copy(alpha = 0.3f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = uiState.error!!,
                                                    color = VermilionRed,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    textAlign = TextAlign.Center,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
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
                                                .height(48.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            enabled = imageCapture != null,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) {
                                            Text("Verify Face & Record Attendance", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
