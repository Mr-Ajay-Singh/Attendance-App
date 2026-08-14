package com.invictus.attendanceapp.feature.staff.presentation.stafflist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.ui.theme.KinpakuGold
import com.invictus.attendanceapp.ui.theme.PatinaTeal
import com.invictus.attendanceapp.ui.theme.VermilionRed
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffListScreen(
    viewModel: StaffListViewModel,
    onAddStaffClick: () -> Unit,
    onStaffClick: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                    text = "Are you sure you want to log out from the administrative session?",
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "STAFF DIRECTORY",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.staffList.size} Enrolled Members",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddStaffClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Staff", fontWeight = FontWeight.Bold) },
                containerColor = KinpakuGold,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(4.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search by name or employee ID...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.staffList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO STAFF MEMBERS FOUND",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.refresh() },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Refresh")
                            }
                            Button(
                                onClick = onAddStaffClick,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Add Staff")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(uiState.staffList, key = { it.id }) { staff ->
                        StaffItemCard(
                            staff = staff,
                            onClick = { onStaffClick(staff.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StaffItemCard(
    staff: Staff,
    onClick: () -> Unit
) {
    val imageSource = staff.faceImageUrl ?: staff.faceImagePath
    val imageModel: Any? = when {
        imageSource.isNullOrBlank() -> null
        imageSource.startsWith("http://") || imageSource.startsWith("https://") -> imageSource
        File(imageSource).exists() -> File(imageSource)
        else -> null
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = staff.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = staff.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "ID: ${staff.employeeId}",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (staff.isFaceEnrolled) {
                Surface(
                    color = PatinaTeal.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, PatinaTeal.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PatinaTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Face Enrolled ✓",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PatinaTeal
                        )
                    }
                }
            } else {
                Surface(
                    color = VermilionRed.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, VermilionRed.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = VermilionRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Not Enrolled",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VermilionRed
                        )
                    }
                }
            }
        }
    }
}
