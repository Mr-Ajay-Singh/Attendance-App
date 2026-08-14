package com.invictus.attendanceapp.feature.staff.presentation.addstaff

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.invictus.attendanceapp.ui.theme.KinpakuGold
import com.invictus.attendanceapp.ui.theme.PatinaTeal
import com.invictus.attendanceapp.ui.theme.VermilionRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffScreen(
    viewModel: AddStaffViewModel,
    onBackClick: () -> Unit,
    onStaffAddedNavigateToEnrollment: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Credentials Summary Modal Dialog
    if (uiState.showCredentialsDialog && uiState.createdStaff != null) {
        val staff = uiState.createdStaff!!
        AlertDialog(
            onDismissRequest = { /* Force explicit user action */ },
            icon = {
                Surface(
                    modifier = Modifier.size(56.dp).clip(CircleShape),
                    color = PatinaTeal.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PatinaTeal.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = PatinaTeal,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "Staff Account Created!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Share these login credentials with ${staff.name}:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = BorderStroke(1.dp, KinpakuGold.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "EMPLOYEE ID: ${staff.employeeId}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = KinpakuGold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "PASSWORD: ${uiState.passwordInput}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = KinpakuGold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val textToCopy = "Employee ID: ${staff.employeeId}\nPassword: ${uiState.passwordInput}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Staff Credentials", textToCopy)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Credentials copied to clipboard!", Toast.LENGTH_SHORT).show()

                        viewModel.dismissCredentialsDialog()
                        onStaffAddedNavigateToEnrollment(staff.id)
                        viewModel.consumeCreatedStaff()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KinpakuGold,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy & Proceed to Face Enrollment", fontWeight = FontWeight.Bold)
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
                        text = "ADD STAFF MEMBER",
                        style = MaterialTheme.typography.labelSmall,
                        color = KinpakuGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Brand Card
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                color = KinpakuGold.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, KinpakuGold.copy(alpha = 0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = KinpakuGold,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "New Staff Member",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Thin
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Create employee account and register biometric profile",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Main Form Container Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Surface(
                        color = KinpakuGold.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, KinpakuGold.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "EMPLOYEE DETAILS",
                            style = MaterialTheme.typography.labelSmall,
                            color = KinpakuGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = uiState.nameInput,
                        onValueChange = { viewModel.onNameChanged(it) },
                        label = { Text("Staff Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = KinpakuGold) },
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KinpakuGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.employeeIdInput,
                        onValueChange = { viewModel.onEmployeeIdChanged(it) },
                        label = { Text("Employee ID (e.g. EMP001)") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = KinpakuGold) },
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KinpakuGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.passwordInput,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = KinpakuGold) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KinpakuGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.generateRandomPassword() }) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = PatinaTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Auto-Generate Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PatinaTeal)
                        }
                    }

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = VermilionRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, VermilionRed.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.error!!,
                                color = VermilionRed,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.addStaff() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(4.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KinpakuGold,
                            contentColor = Color.Black
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text("Create Account & Enroll Face", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
