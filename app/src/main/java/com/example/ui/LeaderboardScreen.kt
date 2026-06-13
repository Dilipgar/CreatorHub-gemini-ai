package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LeaderboardItem(
    val rank: Int,
    val name: String,
    val handle: String,
    val metricValue: String,
    val subMetric: String,
    val isVerified: Boolean
)

@Composable
fun LeaderboardScreen(viewModel: CreatorHubViewModel) {
    var leaderboardCategory by remember { mutableStateOf("top") } // "top" | "trending" | "growing"

    val topCreators = listOf(
        LeaderboardItem(1, "Kabir Vlogs", "@kabirvlogs", "98 Score", "₹12.4 Lakhs", true),
        LeaderboardItem(2, "Ankit Photographer", "@ankitclicks", "94 Score", "₹4.8 Lakhs", true),
        LeaderboardItem(3, "Rhea Chef", "@rheafoodie", "93 Score", "₹3.9 Lakhs", true),
        LeaderboardItem(4, "Dev Codes", "@devcodes", "90 Score", "₹3.2 Lakhs", false),
        LeaderboardItem(5, "Priya Lifestyle", "@priyalife", "88 Score", "₹2.9 Lakhs", true)
    )

    val trendingCreators = listOf(
        LeaderboardItem(1, "Rhea Chef", "@rheafoodie", "9.8% Eng. Rate", "2.1M reach", true),
        LeaderboardItem(2, "Nikhil Tech", "@nikhiltech", "9.4% Eng. Rate", "1.6M reach", true),
        LeaderboardItem(3, "Kabir Vlogs", "@kabirvlogs", "8.9% Eng. Rate", "3.4M reach", true),
        LeaderboardItem(4, "Neha Travel", "@nehatravel", "8.2% Eng. Rate", "800k reach", false),
        LeaderboardItem(5, "Aria Beats", "@ariabeats", "7.9% Eng. Rate", "1.1M reach", true)
    )

    val growingCreators = listOf(
        LeaderboardItem(1, "Dev Codes", "@devcodes", "+142% Weekly", "85k Subs", false),
        LeaderboardItem(2, "Aria Beats", "@ariabeats", "+118% Weekly", "42k Subs", true),
        LeaderboardItem(3, "Nikhil Tech", "@nikhiltech", "+94% Weekly", "120k Subs", true),
        LeaderboardItem(4, "Rhea Chef", "@rheafoodie", "+75% Weekly", "310k Subs", true),
        LeaderboardItem(5, "Priya Lifestyle", "@priyalife", "+48% Weekly", "240k Subs", true)
    )

    val activeList = when (leaderboardCategory) {
        "top" -> topCreators
        "trending" -> trendingCreators
        else -> growingCreators
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Top Header section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CosmicNavy)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Leaderboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Verified Performance Ledger",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandIndigo,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Leaderboard Award",
                    tint = LightSageGlow,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    Triple("top", "Top Creators", Icons.Default.Star),
                    Triple("trending", "Trending", Icons.Default.TrendingUp),
                    Triple("growing", "Fast Growing", Icons.Default.OfflineBolt)
                ).forEach { (catId, label, icon) ->
                    val isSelected = leaderboardCategory == catId
                    Button(
                        onClick = { leaderboardCategory = catId },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) BrandIndigo else Color.Transparent,
                            contentColor = if (isSelected) Color.White else TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Leader Board List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(activeList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavy),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CardNavy)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank Badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    when (item.rank) {
                                        1 -> Color(0xFFFFD700).copy(alpha = 0.15f)
                                        2 -> Color(0xFFC0C0C0).copy(alpha = 0.15f)
                                        3 -> Color(0xFFCD7F32).copy(alpha = 0.15f)
                                        else -> SurfaceNavy
                                    }, shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${item.rank}",
                                color = when (item.rank) {
                                    1 -> Color(0xFFFFD700)
                                    2 -> Color(0xFFE2E2E2)
                                    3 -> Color(0xFFCD7F32)
                                    else -> TextSecondary
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Avatar representation
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(SurfaceNavy, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.name.take(1),
                                color = BrandIndigo,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (item.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Seal",
                                        tint = BrandIndigo,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = item.handle,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        // Score metrics tag
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = item.metricValue,
                                color = LightSageGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = item.subMetric,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
