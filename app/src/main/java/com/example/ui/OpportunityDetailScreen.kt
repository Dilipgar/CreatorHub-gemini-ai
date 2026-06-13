package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OpportunityDetailScreen(viewModel: CreatorHubViewModel) {
    val op by viewModel.selectedOpportunity.collectAsStateWithLifecycle()
    val dealState by viewModel.selectedOpportunityDeal.collectAsStateWithLifecycle()

    var showApplyDialog by remember { mutableStateOf(false) }
    var bidAmount by remember { mutableStateOf("") }

    if (op == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricCyan)
        }
        return
    }

    val opportunity = op!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Upper Detail Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo("opportunities") },
                modifier = Modifier.background(SurfaceNavy, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Opportunity Detail",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main info card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                    border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(CardNavy, CircleShape)
                                    .border(1.2.dp, ElectricCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opportunity.brandName.take(1),
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    opportunity.brandName,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    opportunity.location,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            opportunity.title,
                            color = TextPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlatformLabel(opportunity.platform)
                            TypeLabel(opportunity.type)
                        }
                    }
                }
            }

            // Budget Info Card
            item {
                Card(
                     modifier = Modifier.fillMaxWidth(),
                     shape = RoundedCornerShape(12.dp),
                     colors = CardDefaults.cardColors(containerColor = CardNavy),
                     border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("PROPOSED ESCROW BUDGET", color = TextSecondary, fontSize = 10.sp)
                            Text(
                                opportunity.budgetRange,
                                color = LightSageGlow,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceNavy
                        ) {
                            Text(
                                opportunity.durationText,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // About Campaign Section
            item {
                Column {
                    Text(
                        "About the Campaign",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        opportunity.aboutCampaign,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }

            // Requirements List Section
            item {
                Column {
                    Text(
                        "Requirements",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
                    ) {
                        Text(
                            opportunity.requirements,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Space filler
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }

        // Action panel at bottom of screening detail
        Surface(
            color = SurfaceNavy,
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Save button (Heart Outline / Full)
                OutlinedIconButton(
                    onClick = { viewModel.toggleSaveOpportunity(opportunity) },
                    border = BorderStroke(1.5.dp, NeonPurple),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (opportunity.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Save Campaign",
                        tint = if (opportunity.isSaved) CoralRed else NeonPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Primary Apply CTA button
                val currentDealStatus = dealState?.status
                val btnColor = if (currentDealStatus != null) LightSageGlow else ElectricCyan
                val btnText = when (currentDealStatus) {
                    "Applied" -> "Applied (Awaiting Brief)"
                    "Accepted" -> "Partnership Active! Go to Deals"
                    "Work Submitted" -> "Review in Progress"
                    "Under Review" -> "Under Review"
                    "Completed" -> "Deal Completed"
                    "Payment Released" -> "Escrow Released (Paid)"
                    else -> "Apply for Campaign"
                }

                Button(
                    onClick = {
                        if (currentDealStatus == null) {
                            showApplyDialog = true
                        } else {
                            viewModel.navigateTo("deals")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("apply_detail_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = btnText,
                        color = CosmicNavy,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    // Modal dialog to submit proposed budget bid
    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = { Text("Apply for Campaign", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Submit a pitch proposing your commercial deal value. The funds will be stored securely in the CreatorHub Escrow.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = bidAmount,
                        onValueChange = { bidAmount = it },
                        label = { Text("Proposed Budget (e.g. ₹50,000)", color = ElectricCyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NeonPurple
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (bidAmount.isNotBlank()) {
                            viewModel.applyForOpportunity(
                                opportunity.id,
                                opportunity.title,
                                opportunity.brandName,
                                bidAmount
                            )
                            showApplyDialog = false
                            // Navigates directly to chats to show that session was started!
                            viewModel.navigateTo("messages")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Submit Application", color = CosmicNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApplyDialog = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            },
            containerColor = SurfaceNavy,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
