package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NotificationCenterScreen(viewModel: CreatorHubViewModel) {
    val notificationList by viewModel.notifications.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo("opportunities") },
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceNavy, CircleShape)
                    .testTag("notification_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandIndigo
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Notification Center",
                    style = MaterialTheme.typography.titleLarge,
                    color = BrandIndigo,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Sync milestones and contract escrow events",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppGray
                )
            }
            if (notificationList.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.notifications.value = emptyList() },
                    modifier = Modifier.testTag("clear_notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear All",
                        tint = CoralRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = CardNavy.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))

        if (notificationList.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "No Notifications",
                        tint = AppGray,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Hub is up to date!",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "No new contract alerts or deal milestone announcements.",
                        color = AppGray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notificationList) { item ->
                    val (icon, tint) = when (item.type) {
                        "deal" -> Pair(Icons.Default.AccountBalanceWallet, NeonPurple)
                        "campaign" -> Pair(Icons.Default.Campaign, ElectricCyan)
                        "profile" -> Pair(Icons.Default.Person, LightSageGlow)
                        "system" -> Pair(Icons.Default.Info, BrandIndigo)
                        else -> Pair(Icons.Default.Notifications, ElectricCyan)
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicNavy),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, CardNavy),
                        modifier = Modifier.fillMaxWidth().testTag("notification_item_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(tint.copy(alpha = 0.1f), CircleShape)
                                    .border(1.dp, tint.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = item.type,
                                    tint = tint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        color = TextPrimary,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = item.timestamp,
                                        color = AppGray,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.body,
                                    color = AppGray,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationBellWithDot(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(SurfaceNavy, CircleShape)
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("notification_bell"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = TextPrimary
        )
        // Red Dot indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(CoralRed, CircleShape)
                .align(Alignment.TopEnd)
        )
    }
}
