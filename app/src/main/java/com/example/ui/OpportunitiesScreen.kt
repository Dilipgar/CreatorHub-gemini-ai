package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.OpportunityEntity

@Composable
fun OpportunitiesFeedScreen(viewModel: CreatorHubViewModel) {
    val searchVal by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeTabFilter by viewModel.activeTabFilter.collectAsStateWithLifecycle()
    val opportunities by viewModel.filteredOpportunities.collectAsStateWithLifecycle()

    val tabs = listOf("All", "Brand Deals", "Affiliate", "Collab", "Contest")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Feed Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Opportunities",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Text(
                    text = "Discover campaigns matching your niche",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            NotificationBellWithDot(onClick = { viewModel.navigateTo("notifications") })
        }

        // Search Bar Area
        TextField(
            value = searchVal,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search brands, platforms, titles...", color = AppGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ElectricCyan) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceNavy,
                unfocusedContainerColor = SurfaceNavy,
                focusedIndicatorColor = ElectricCyan,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .testTag("search_bar")
        )

        // Horizontal filter chips
        LazyRow(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs) { tab ->
                val isSelected = tab == activeTabFilter
                val backColor = if (isSelected) ElectricCyan else SurfaceNavy
                val textColor = if (isSelected) Color.White else TextSecondary
                val strokeColor = if (isSelected) Color.Transparent else NeonPurple.copy(alpha = 0.5f)

                Surface(
                    modifier = Modifier.clickable { viewModel.setActiveTab(tab) },
                    shape = RoundedCornerShape(20.dp),
                    color = backColor,
                    border = BorderStroke(1.dp, strokeColor)
                ) {
                    Text(
                        text = tab,
                        color = textColor,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Discovery View: Industry/Niche Heading & Chips
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                text = "Filter by Industry",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            val industries = listOf("All", "Consumer Tech", "Design SaaS", "Hosting & Cloud", "Enterprise SaaS", "E-commerce Retail", "Travel & Tourism", "Adventure Tech", "Camera Hardware")
            val selectedInd by viewModel.selectedIndustry.collectAsStateWithLifecycle()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("industry_filter_row")
            ) {
                items(industries) { ind ->
                    val isSelected = ind == selectedInd
                    val backColor = if (isSelected) NeonPurple else SurfaceNavy
                    val textColor = if (isSelected) Color.White else TextSecondary
                    val strokeColor = if (isSelected) Color.Transparent else NeonPurple.copy(alpha = 0.3f)
                    val displayLabel = if (ind == "All") "🌐 All Niches" else ind

                    Surface(
                        modifier = Modifier
                            .clickable { viewModel.setSelectedIndustry(ind) }
                            .testTag("industry_chip_${ind.lowercase().replace(" ", "_").replace("&", "and")}"),
                        shape = RoundedCornerShape(16.dp),
                        color = backColor,
                        border = BorderStroke(1.dp, strokeColor)
                    ) {
                        Text(
                            text = displayLabel,
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Discovery View: Budget Heading & Chips
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                text = "Filter by Budget Range",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            val budgets = listOf(
                "Any" to "💰 Any Budget",
                "High Fixed (₹25k+)" to "💎 Premium Pay (₹25k+)",
                "Commission/Affiliate" to "📈 Performance Comms",
                "Paid Collaboration" to "🤝 Paid Collab"
            )
            val selectedBug by viewModel.selectedBudgetRange.collectAsStateWithLifecycle()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("budget_filter_row")
            ) {
                items(budgets) { (key, label) ->
                    val isSelected = key == selectedBug
                    val backColor = if (isSelected) ElectricCyan else SurfaceNavy
                    val textColor = if (isSelected) Color.Black else TextSecondary
                    val strokeColor = if (isSelected) Color.Transparent else ElectricCyan.copy(alpha = 0.3f)

                    Surface(
                        modifier = Modifier
                            .clickable { viewModel.setSelectedBudgetRange(key) }
                            .testTag("budget_chip_${key.lowercase().replace(" ", "_").replace("(", "").replace(")", "").replace("+", "plus").replace("/", "_")}"),
                        shape = RoundedCornerShape(16.dp),
                        color = backColor,
                        border = BorderStroke(1.dp, strokeColor)
                    ) {
                        Text(
                            text = label,
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats summary panel on top of feed as shown in blueprint header
            item {
                FeedHeroAndStatsSection()
            }

            if (opportunities.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.WorkOutline,
                            contentDescription = "Empty",
                            tint = AppGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No campaigns found",
                            color = TextSecondary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Try adjusting your search queries or category filters",
                            color = AppGray,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                items(opportunities) { op ->
                    OpportunityItemCard(
                        opportunity = op,
                        onCardClick = { viewModel.selectOpportunity(op.id) },
                        onSaveToggle = { viewModel.toggleSaveOpportunity(op) }
                    )
                }
            }
        }
    }
}

@Composable
fun FeedHeroAndStatsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BrandGradient)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "India's Professional Network for Creators",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Land secure escrow brand deals and certified affiliate contracts instantly.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Campaigns Growth",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        // 4 Creator statistics cards (Goal 6, section 2)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                StatCard(value = "15,240+", label = "Active Creators", icon = Icons.Default.People)
            }
            item {
                StatCard(value = "1,420+", label = "Deals Posted", icon = Icons.Default.Work)
            }
            item {
                StatCard(value = "380+", label = "Affiliates", icon = Icons.Default.Store)
            }
            item {
                StatCard(value = "4,890+", label = "Verified Stars", icon = Icons.Default.Verified)
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, icon: ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(135.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(LightSageGlow, CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun OpportunityItemCard(
    opportunity: OpportunityEntity,
    onCardClick: () -> Unit,
    onSaveToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("opportunity_card_${opportunity.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
        border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand label block
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CardNavy, CircleShape)
                            .border(1.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opportunity.brandName.take(1),
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = opportunity.brandName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = ElectricCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Brand",
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Heart Save icon
                IconButton(onClick = onSaveToggle) {
                    Icon(
                        imageVector = if (opportunity.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Save Opportunity",
                        tint = if (opportunity.isSaved) CoralRed else AppGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Opportunity Title
            Text(
                text = opportunity.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Platform tag + Type tag
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlatformLabel(platform = opportunity.platform)
                TypeLabel(type = opportunity.type)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider separating stats and budget
            HorizontalDivider(color = NeonPurple.copy(alpha = 0.4f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Budget Escrow",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = opportunity.budgetRange,
                        color = LightSageGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardNavy,
                    border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = opportunity.durationText,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            if (opportunity.commissionRate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = "Commission rate",
                        tint = LightSageGlow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = opportunity.commissionRate,
                        color = LightSageGlow,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // High polish section metrics for Category and Difficulty (Section 7)
            if (opportunity.category.isNotEmpty() || opportunity.difficultyLevel.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (opportunity.category.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceNavy,
                            border = BorderStroke(0.5.dp, NeonPurple.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = opportunity.category,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (opportunity.difficultyLevel.isNotEmpty()) {
                        val diffColor = when {
                            opportunity.difficultyLevel.contains("Beginner") -> LightSageGlow
                            opportunity.difficultyLevel.contains("Intermediate") -> ElectricCyan
                            else -> NeonPurple
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = diffColor.copy(alpha = 0.1f),
                            border = BorderStroke(0.5.dp, diffColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = opportunity.difficultyLevel,
                                color = diffColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = NeonPurple.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Premium Quick actions row fulfilling Goal 6 & 7
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSaveToggle,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (opportunity.isSaved) CoralRed else TextSecondary),
                    border = BorderStroke(1.dp, if (opportunity.isSaved) CoralRed.copy(alpha = 0.5f) else NeonPurple.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = if (opportunity.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Save icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (opportunity.isSaved) "Saved" else "Save Deal", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onCardClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text("Apply Now", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Apply", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun PlatformLabel(platform: String) {
    val tint = when (platform.lowercase()) {
        "youtube" -> Color(0xFFFF0000)
        "instagram" -> Color(0xFFE1306C)
        "canva pro", "creative" -> Color(0xFF00C4CC)
        else -> ElectricCyan
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = tint.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = when (platform.lowercase()) {
                    "youtube" -> Icons.Default.PlayArrow
                    "instagram" -> Icons.Default.CameraAlt
                    else -> Icons.Default.Language
                },
                contentDescription = platform,
                tint = tint,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = platform,
                color = tint,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun TypeLabel(type: String) {
    val tint = when (type) {
        "Brand Deal" -> ElectricCyan
        "Affiliate" -> NeonPurple
        "Collab" -> LightSageGlow
        else -> Color.Gray
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = tint.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.4f))
    ) {
        Text(
            text = type,
            color = tint,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
