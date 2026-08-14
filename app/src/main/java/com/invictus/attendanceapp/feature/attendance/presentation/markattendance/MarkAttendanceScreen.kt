package com.invictus.attendanceapp.feature.attendance.presentation.markattendance

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
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
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.invictus.attendanceapp.core.camera.CameraPreview
import com.invictus.attendanceapp.core.camera.takePictureBitmap
import com.invictus.attendanceapp.core.common.openAppPermissionSettings
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
    var showPermissionRationaleDialog by remember { mutableStateOf(false) }
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

    // Permission Denied / Rationale Dialog
    if (showPermissionRationaleDialog && !permissionsState.allPermissionsGranted) {
        AlertDialog(
            onDismissRequest = { showPermissionRationaleDialog = false },
            icon = {
                Surface(
                    modifier = Modifier.size(56.dp).clip(CircleShape),
                    color = VermilionRed.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, VermilionRed.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = VermilionRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "Compulsory Permissions Required",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Camera and GPS Location permissions are compulsory to capture your biometric identity and verify check-in coordinates.\n\nPlease enable them in App Settings to proceed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationaleDialog = false
                        openAppPermissionSettings(context)
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KinpakuGold,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Open Permissions Settings", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showPermissionRationaleDialog = false },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                Surface(
                    modifier = Modifier.size(56.dp).clip(CircleShape),
                    color = PatinaTeal.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PatinaTeal.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PatinaTeal,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
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
                                .size(96.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                    Surface(
                        color = PatinaTeal.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, PatinaTeal.copy(alpha = 0.25f))
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Time: ${timeFormat.format(Date(attendance.timestamp))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Location: Lat ${String.format(Locale.US, "%.5f", attendance.latitude)}, Long ${String.format(Locale.US, "%.5f", attendance.longitude)}",
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
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KinpakuGold,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
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
                        color = KinpakuGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onBackground
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
                    CircularProgressIndicator(
                        color = KinpakuGold,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.5.dp
                    )
                }
            } else if (staff != null && !staff.faceEnrolled) {
                // Biometric Registration Pending Screen
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
                                modifier = Modifier.size(48.dp),
                                tint = VermilionRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Biometric Not Registered",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Thin
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${staff.name} • ${staff.employeeId}",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, VermilionRed.copy(alpha = 0.4f)),
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
            } else if (uiState.isMarkedToday) {
                // Screen State: Attendance Marked For Today (Allows multi-punch)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape),
                        color = PatinaTeal.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, PatinaTeal.copy(alpha = 0.35f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = PatinaTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Attendance Marked for Today",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center
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

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, PatinaTeal.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = PatinaTeal.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "RECORDED TODAY ✓",
                                        color = PatinaTeal,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }


                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            uiState.latestTodayAttendance?.let { latest ->
                                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())

                                Text(
                                    text = "Last Entry: ${timeFormat.format(Date(latest.timestamp))}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dateFormat.format(Date(latest.timestamp)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Multi-punch Action Button: "+ Mark Another Entry"
                    Button(
                        onClick = {
                            if (permissionsState.allPermissionsGranted) {
                                isFaceDetected = false
                                capturedBitmap = null
                                viewModel.openCamera()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Camera and Location permissions are compulsory to mark attendance.",
                                    Toast.LENGTH_LONG
                                ).show()
                                permissionsState.launchMultiplePermissionRequest()
                                showPermissionRationaleDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KinpakuGold,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("+ Mark Another Entry", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Initial Mark Attendance Screen (No punches today yet)
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
                                Toast.makeText(
                                    context,
                                    "Camera and Location permissions are compulsory to mark attendance.",
                                    Toast.LENGTH_LONG
                                ).show()
                                permissionsState.launchMultiplePermissionRequest()
                                showPermissionRationaleDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KinpakuGold,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Mark Attendance", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
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
                                            text = "VERIFYING BIOMETRIC PROFILE...",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
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
                                            .border(BorderStroke(3.dp, frameBorderColor), CircleShape)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Face Detection Status Pill
                                    Surface(
                                        color = if (isFaceDetected) PatinaTeal.copy(alpha = 0.9f) else VermilionRed.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (isFaceDetected) "FACE DETECTED ✓" else "POSITION FACE INSIDE FRAME",
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
                                        text = "Center face inside frame & capture",
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
                                                style = MaterialTheme.typography.bodySmall,
                                                textAlign = TextAlign.Center,
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
                                            containerColor = KinpakuGold,
                                            contentColor = Color.Black
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
