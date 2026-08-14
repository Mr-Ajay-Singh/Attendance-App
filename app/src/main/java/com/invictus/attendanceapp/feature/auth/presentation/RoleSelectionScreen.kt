package com.invictus.attendanceapp.feature.auth.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LockPerson
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole
import com.invictus.attendanceapp.ui.theme.KinpakuGold
import com.invictus.attendanceapp.ui.theme.PatinaTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "ATTENDANCE PORTAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Icon Circle
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LockPerson,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Eyebrow Badge
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "PRIVACY-FIRST ON-DEVICE BIOMETRICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Welcome",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Thin
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select your portal to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Admin Portal Choice Card
            RoleCard(
                badge = "MANAGEMENT & ENROLLMENT",
                title = "Admin Portal",
                description = "Manage staff directory, enroll face embeddings, and audit verified location history.",
                icon = Icons.Default.AdminPanelSettings,
                accentColor = KinpakuGold,
                onClick = { onRoleSelected(UserRole.ADMIN) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Staff Portal Choice Card
            RoleCard(
                badge = "DAILY ATTENDANCE VERIFICATION",
                title = "Staff Portal",
                description = "Quick selfie-based face attendance verification with real-time GPS location capture.",
                icon = Icons.Default.Badge,
                accentColor = PatinaTeal,
                onClick = { onRoleSelected(UserRole.STAFF) }
            )
        }
    }
}

@Composable
private fun RoleCard(
    badge: String,
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = accentColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}
