package com.invictus.attendanceapp.feature.staff.presentation.enrollment

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.invictus.attendanceapp.core.camera.CameraPreview
import com.invictus.attendanceapp.core.camera.takePictureBitmap
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FaceEnrollmentScreen(
    viewModel: FaceEnrollmentViewModel,
    onBackClick: () -> Unit,
    onEnrollmentSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isFaceDetected by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Reset captured bitmap if processing finished with error
    LaunchedEffect(uiState.isProcessing, uiState.error) {
        if (!uiState.isProcessing && uiState.error != null) {
            capturedBitmap = null
        }
    }

    if (uiState.isSuccess) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Enrollment Successful!", fontWeight = FontWeight.Bold) },
            text = { Text("Face biometric embedding has been securely saved for ${uiState.staff?.name ?: "Staff"}.") },
            confirmButton = {
                Button(onClick = onEnrollmentSuccess) {
                    Text("Done")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Face Enrollment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !uiState.isProcessing
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (cameraPermissionState.status.isGranted) {
            val frameBorderColor by animateColorAsState(
                targetValue = if (isFaceDetected) Color(0xFF43A047) else Color(0xFFE53935),
                animationSpec = tween(300),
                label = "enrollBorderColor"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // If processing, shut down camera completely and display frozen captured selfie
                if (uiState.isProcessing && capturedBitmap != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Face",
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
                                    text = "Enrolling biometric profile...",
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

                    // Face Overlay Frame Guide with Real-time Green/Red feedback
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 100.dp),
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
                                    text = if (isFaceDetected) "Face Detected ✓" else "Position Face Inside Circle",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Instruction & Controls Bar
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
                                text = "Position face inside frame & capture",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            uiState.staff?.let { staff ->
                                Text(
                                    text = "Enrolling for ${staff.name} (${staff.employeeId})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (uiState.error != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.error!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
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
                                                viewModel.captureAndEnroll(bitmap)
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
                                Icon(Icons.Default.Camera, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Capture & Enroll Face", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Camera permission is required to enroll staff faces.",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}
