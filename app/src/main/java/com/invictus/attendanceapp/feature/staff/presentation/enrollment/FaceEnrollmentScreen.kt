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
import com.invictus.attendanceapp.ui.theme.PatinaTeal
import com.invictus.attendanceapp.ui.theme.VermilionRed
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
                    tint = PatinaTeal,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Enrollment Successful!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Biometric face embedding has been securely extracted and saved for ${uiState.staff?.name ?: "Staff"}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = onEnrollmentSuccess,
                    shape = RoundedCornerShape(4.dp)
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
                        text = "FACE ENROLLMENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !uiState.isProcessing
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
        if (cameraPermissionState.status.isGranted) {
            val frameBorderColor by animateColorAsState(
                targetValue = if (isFaceDetected) PatinaTeal else VermilionRed,
                animationSpec = tween(300),
                label = "enrollBorderColor"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
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
                                    text = "ENROLLING BIOMETRIC EMBEDDING...",
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

                    // Face Overlay Frame
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

                    // Bottom Instruction Sheet Drawer
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
                                text = "Position face inside circle & capture",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )

                            uiState.staff?.let { staff ->
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Enrolling for ${staff.name} (${staff.employeeId})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

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
                                                viewModel.captureAndEnroll(bitmap)
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
                                Icon(Icons.Default.Camera, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Capture & Enroll Face", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}
