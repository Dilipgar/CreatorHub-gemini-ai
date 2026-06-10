package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ChatSessionEntity
import com.example.data.DealEntity
import com.example.data.MessageEntity
import com.example.data.OpportunityEntity
import com.example.data.UserProfile

// --- Brand Theme Colors matching the Premium Startup-Quality Gradient theme ---
val DarkNavy = Color(0xFFF8FAFC) // Premium slate light background
val SurfaceNavy = Color(0xFFF1F5F9) // Soft container slate background
val CardNavy = Color(0xFFE2E8F0) // Slate boundary border/fill
val ElectricCyan = Color(0xFF4F46E5) // Primary Indigo Brand Color (LinkedIn/Upwork feel)
val CosmicNavy = Color(0xFFFFFFFF) // White contrast
val NeonPurple = Color(0xFF7C3AED) // Premium Purple Brand Accent (Startup Gradient feel)
val CoralRed = Color(0xFFEF4444) // Premium Red
val LightSageGlow = Color(0xFF10B981) // Premium Emerald green success

// Brand gradient brush
val BrandIndigo = Color(0xFF4F46E5)
val BrandPurple = Color(0xFF7C3AED)
val BrandGradient = Brush.horizontalGradient(listOf(BrandIndigo, BrandPurple))

@Composable
fun CreatorHubLogo(
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 44.dp,
    showWordmark: Boolean = true,
    fontSize: androidx.compose.ui.unit.TextUnit = 22.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .background(BrandGradient, RoundedCornerShape(12.dp))
                .padding(iconSize / 5),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Hub,
                contentDescription = "CreatorHub Connection Icon",
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        if (showWordmark) {
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Creator",
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = fontSize,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Hub",
                    color = BrandPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize,
                    letterSpacing = (-0.5).sp
                )
            }
        }
    }
}

// Custom specific text colors
val TextPrimary = Color(0xFF1A1C1E) // Slate text primary
val TextSecondary = Color(0xFF44474E) // Neutral text secondary
val AppGray = Color(0xFF74777F) // Accessible subtext gray

@Composable
fun AppNavigation(viewModel: CreatorHubViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        when (currentScreen) {
            "onboarding" -> OnboardingScreen(viewModel)
            "login" -> LoginScreen(viewModel)
            "signup" -> SignupScreen(viewModel)
            "forgot_password" -> ForgotPasswordScreen(viewModel)
            "opportunities" -> MainAppScaffold(viewModel, "opportunities") { OpportunitiesFeedScreen(viewModel) }
            "details" -> MainAppScaffold(viewModel, "opportunities") { OpportunityDetailScreen(viewModel) }
            "leaderboard" -> MainAppScaffold(viewModel, "leaderboard") { LeaderboardScreen(viewModel) }
            "messages" -> MainAppScaffold(viewModel, "messages") { ChatListScreen(viewModel) }
            "chat_detail" -> MainAppScaffold(viewModel, "messages") { ChatDetailScreen(viewModel) }
            "create" -> MainAppScaffold(viewModel, "create") { CreateOpportunityScreen(viewModel) }
            "deals" -> MainAppScaffold(viewModel, "deals") { DealStatusScreen(viewModel) }
            "profile" -> MainAppScaffold(viewModel, "profile") { CreatorProfileScreen(viewModel) }
        }
    }
}

@Composable
fun MainAppScaffold(
    viewModel: CreatorHubViewModel,
    activeTab: String,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceNavy,
                contentColor = TextSecondary,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = activeTab == "opportunities",
                    onClick = { viewModel.navigateTo("opportunities") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Feed", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = AppGray,
                        unselectedTextColor = AppGray,
                        indicatorColor = CardNavy
                    ),
                    modifier = Modifier.testTag("nav_opportunities")
                )
                NavigationBarItem(
                    selected = activeTab == "leaderboard",
                    onClick = { viewModel.navigateTo("leaderboard") },
                    icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboard") },
                    label = { Text("Leaders", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = AppGray,
                        unselectedTextColor = AppGray,
                        indicatorColor = CardNavy
                    ),
                    modifier = Modifier.testTag("nav_leaderboard")
                )
                NavigationBarItem(
                    selected = activeTab == "messages",
                    onClick = { viewModel.navigateTo("messages") },
                    icon = { Icon(Icons.Default.Message, contentDescription = "Messages") },
                    label = { Text("Chats", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = AppGray,
                        unselectedTextColor = AppGray,
                        indicatorColor = CardNavy
                    ),
                    modifier = Modifier.testTag("nav_messages")
                )
                NavigationBarItem(
                    selected = activeTab == "create",
                    onClick = { viewModel.navigateTo("create") },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Create", tint = ElectricCyan, modifier = Modifier.size(24.dp)) },
                    label = { Text("Post", style = MaterialTheme.typography.labelSmall, color = ElectricCyan) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = AppGray,
                        unselectedTextColor = AppGray,
                        indicatorColor = CardNavy
                    ),
                    modifier = Modifier.testTag("nav_create")
                )
                NavigationBarItem(
                    selected = activeTab == "deals",
                    onClick = { viewModel.navigateTo("deals") },
                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Deals") },
                    label = { Text("Deals", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = AppGray,
                        unselectedTextColor = AppGray,
                        indicatorColor = CardNavy
                    ),
                    modifier = Modifier.testTag("nav_deals")
                )
                NavigationBarItem(
                    selected = activeTab == "profile",
                    onClick = { viewModel.navigateTo("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = AppGray,
                        unselectedTextColor = AppGray,
                        indicatorColor = CardNavy
                    ),
                    modifier = Modifier.testTag("nav_profile")
                )
            }
        },
        containerColor = DarkNavy,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            content()
        }
    }
}

// --- SCREEN 1: ONBOARDING SCREEN ---
@Composable
fun OnboardingScreen(viewModel: CreatorHubViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.4f))

        // Premium Startup Logo and Wordmark
        CreatorHubLogo(
            iconSize = 64.dp,
            fontSize = 32.sp,
            showWordmark = true
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Professional Connected Startup-Style Visual Illustration
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .border(BorderStroke(1.dp, CardNavy), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Connecting Hub Visual Drawing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White, CircleShape)
                            .border(1.5.dp, BrandIndigo, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Creator", tint = BrandIndigo, modifier = Modifier.size(24.dp))
                    }
                    
                    // Connecting dotted gradient line
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(2.dp)
                            .background(BrandGradient)
                    )

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(BrandGradient, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = "Verified Seal", tint = Color.White, modifier = Modifier.size(24.dp))
                    }

                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(2.dp)
                            .background(BrandGradient)
                    )

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White, CircleShape)
                            .border(1.5.dp, BrandPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Handshake, contentDescription = "Brand Deal", tint = BrandPurple, modifier = Modifier.size(24.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Trusted Profiles", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Text("Secure Escrow", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Text("Instant Deals", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Professional Tagline header (Section 8)
        Text(
            text = "India's Professional Network for Creators",
            style = MaterialTheme.typography.titleMedium,
            color = BrandIndigo,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Build Your Creator Career.\nDiscover Opportunities.\nVerify Your Success.",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 32.sp,
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Premium Primary Active Button
        Button(
            onClick = { viewModel.navigateTo("opportunities") },
            colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo, contentColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("get_started_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Enter Creator Exchange",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Premium Outlined Active Button
        OutlinedButton(
            onClick = {
                viewModel.navigateTo("create")
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPurple),
            border = BorderStroke(1.5.dp, BrandPurple),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("im_brand_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Post Campaign as Brand / Agency",
                style = MaterialTheme.typography.titleMedium,
                color = BrandPurple,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- SCREEN 2: OPPORTUNITIES FEED SCREEN ---
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
            NotificationBellWithDot()
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
                .padding(vertical = 8.dp)
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
fun NotificationBellWithDot() {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(SurfaceNavy, CircleShape)
            .clickable { /* action */ }
            .padding(10.dp),
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

// --- SCREEN 3: OPPORTUNITY DETAIL SCREEN ---
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
                                    opportunity.brandName.take(1),
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

// --- SCREEN 4: CREATOR PROFILE SCREEN (Matches screenshot #4) ---
@Composable
fun CreatorProfileScreen(viewModel: CreatorHubViewModel) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    var isEditing by remember { mutableStateOf(false) }

    var editName by remember(profile) { mutableStateOf(profile?.name ?: "Ankit Photographer") }
    var editHandle by remember(profile) { mutableStateOf(profile?.handle ?: "@ankitclicks") }
    var editLocation by remember(profile) { mutableStateOf(profile?.location ?: "Mumbai, IN") }
    var editBio by remember(profile) { mutableStateOf(profile?.bio ?: "I generate premium lifestyle content and social media vlogs focusing on architectural landmarks, drone landscape cinematography, travel storytelling, and luxury retreats. Dedicated to delivering high-CTR assets with zero-dispute contract compliance.") }
    var editInsta by remember(profile) { mutableStateOf(profile?.instagramFollowers ?: "125K") }
    var editYt by remember(profile) { mutableStateOf(profile?.youtubeSubscribers ?: "92K") }
    var editTwitter by remember(profile) { mutableStateOf(profile?.twitterFollowers ?: "14K") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Premium Profile Header with Native Logout and Edit Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "My Hub Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Manage your professional portal",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {
                        if (isEditing) {
                            val updated = (profile ?: UserProfile()).copy(
                                name = editName,
                                handle = editHandle,
                                location = editLocation,
                                bio = editBio,
                                instagramFollowers = editInsta,
                                youtubeSubscribers = editYt,
                                twitterFollowers = editTwitter
                            )
                            viewModel.updateUserProfile(updated)
                            isEditing = false
                        } else {
                            isEditing = true
                        }
                    },
                    modifier = Modifier.testTag("profile_edit_button")
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Save Profile" else "Edit Profile",
                        tint = ElectricCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
                IconButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.testTag("profile_logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = CoralRed,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
        
        HorizontalDivider(color = CardNavy.copy(alpha = 0.5f))

        if (isEditing) {
            // Edit Mode Fields
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Profile Info",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Display Name", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name")
                )

                OutlinedTextField(
                    value = editHandle,
                    onValueChange = { editHandle = it },
                    label = { Text("Handle", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_handle")
                )

                OutlinedTextField(
                    value = editLocation,
                    onValueChange = { editLocation = it },
                    label = { Text("Location", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_location")
                )

                OutlinedTextField(
                    value = editBio,
                    onValueChange = { editBio = it },
                    label = { Text("Professional Bio", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_bio"),
                    singleLine = false,
                    maxLines = 4
                )

                Text(
                    text = "Social Media Stats",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editInsta,
                        onValueChange = { editInsta = it },
                        label = { Text("Instagram", color = ElectricCyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NeonPurple
                        ),
                        modifier = Modifier.weight(1f).testTag("edit_profile_insta")
                    )
                    OutlinedTextField(
                        value = editYt,
                        onValueChange = { editYt = it },
                        label = { Text("YouTube", color = ElectricCyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NeonPurple
                        ),
                        modifier = Modifier.weight(1f).testTag("edit_profile_yt")
                    )
                    OutlinedTextField(
                        value = editTwitter,
                        onValueChange = { editTwitter = it },
                        label = { Text("Twitter", color = ElectricCyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NeonPurple
                        ),
                        modifier = Modifier.weight(1f).testTag("edit_profile_twitter")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updated = (profile ?: UserProfile()).copy(
                            name = editName,
                            handle = editHandle,
                            location = editLocation,
                            bio = editBio,
                            instagramFollowers = editInsta,
                            youtubeSubscribers = editYt,
                            twitterFollowers = editTwitter
                        )
                        viewModel.updateUserProfile(updated)
                        isEditing = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_profile_button")
                ) {
                    Text("Save and Sync Profile", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        } else {
            // Main profile listing
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Profile Card (Header, Avatar, Verification)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicNavy),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, CardNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Profile image with glowing Indigo border
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(SurfaceNavy, CircleShape)
                                        .border(2.5.dp, BrandIndigo, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_creator_avatar),
                                        contentDescription = "Creator profile picture",
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            editName,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified Creator badge",
                                            tint = BrandIndigo,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        "$editHandle  •  $editLocation",
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = BrandIndigo.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                "Travel Cinema",
                                                color = BrandIndigo,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = LightSageGlow.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                "Elite Partner",
                                                color = LightSageGlow,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = CardNavy.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Social Media Links (Goal 4)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Quick Handles", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    SingleSocialLink(name = "Instagram", handle = editInsta, color = Color(0xFFE1306C))
                                    SingleSocialLink(name = "YouTube", handle = editYt, color = Color(0xFFFF0000))
                                    SingleSocialLink(name = "Twitter", handle = editTwitter, color = Color(0xFF1DA1F2))
                                }
                            }
                        }
                    }
                }

                // Trust & Verification Index (Goal 3 & 4)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicNavy),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, CardNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = "Trust", tint = BrandPurple, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Trust & Verification",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BrandPurple.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        "Platinum Verified",
                                        color = BrandPurple,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Creator Score
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Creator Score", fontSize = 11.sp, color = TextSecondary)
                                        Text("${profile?.creatorScore ?: 94}/100", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandIndigo)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { (profile?.creatorScore ?: 94).toFloat() / 100f },
                                        color = BrandIndigo,
                                        trackColor = SurfaceNavy,
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                                    )
                                }

                                // Trust Score
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Trust Rating", fontSize = 11.sp, color = TextSecondary)
                                        Text("${profile?.trustRating ?: 98}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightSageGlow)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { (profile?.trustRating ?: 98).toFloat() / 100f },
                                        color = LightSageGlow,
                                        trackColor = SurfaceNavy,
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(LightSageGlow, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Zero Escrow Disputes  •  Fast Deliverables Score", fontSize = 10.sp, color = AppGray, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // Escrow Earnings Showcase Board (Goal 4 - Earnings showcase)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicNavy),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, CardNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Bank", tint = LightSageGlow, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Escrow Earnings Board",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Icon(Icons.Default.LockClock, contentDescription = "Security", tint = AppGray, modifier = Modifier.size(16.dp))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Total Secured", fontSize = 11.sp, color = TextSecondary)
                                    Text(profile?.totalSecuredEarnings ?: "₹4,85,000", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = LightSageGlow)
                                }
                                Box(modifier = Modifier.width(1.dp).height(40.dp).background(CardNavy))
                                Column {
                                    Text("Held in Escrow", fontSize = 11.sp, color = TextSecondary)
                                    Text(profile?.heldInEscrow ?: "₹75,000", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = BrandIndigo)
                                }
                                Box(modifier = Modifier.width(1.dp).height(40.dp).background(CardNavy))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Contracts", fontSize = 11.sp, color = TextSecondary)
                                    Text("${profile?.activeContractsCount ?: 8} Active", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                                }
                            }
                        }
                    }
                }

                // Profile bio / About Me
                item {
                    Column {
                        Text(
                            "Creator Professional Bio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            editBio,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Past Campaigns Metrics Board (Goal 4 - Campaign results)
                item {
                    Column {
                        Text(
                            "Past Campaign Deliverables",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicNavy),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, CardNavy),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                CampaignOutcomeRow(campaign = "GoPro Trail Expedition", stat = "5.4% Average CTR", views = "1.2M Impressions")
                                CampaignOutcomeRow(campaign = "Canon India Vlogging Kit", stat = "8.2% Engagement Rate", views = "250k Active Views")
                                CampaignOutcomeRow(campaign = "boAt Lifestyle Review", stat = "6.8% Average CTR", views = "180k Organic Reach")
                            }
                        }
                    }
                }

                // Top Platforms (Vlogs, Photos, Feed)
                item {
                    Column {
                        Text(
                            "Platform Distributions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                            border = BorderStroke(1.dp, CardNavy)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                PlatformProgressRow(platform = "YouTube Vlog Channels", ratio = 0.85f, metricLabel = editYt)
                                PlatformProgressRow(platform = "Instagram Photo Feed", ratio = 0.95f, metricLabel = editInsta)
                                PlatformProgressRow(platform = "Twitter Travel Broadcasts", ratio = 0.50f, metricLabel = editTwitter)
                            }
                        }
                    }
                }

                // Featured Portfolio
                item {
                    Column {
                        Text(
                            "Featured Portfolio Highlights",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DummyPortfolioItem(color1 = NeonPurple, color2 = ElectricCyan, label = "Cinematic Reels")
                            DummyPortfolioItem(color1 = Color(0xFFFF5E62), color2 = Color(0xFFFF9966), label = "Desert Forts")
                            DummyPortfolioItem(color1 = Color(0xFF11998E), color2 = Color(0xFF38EF7D), label = "Aero Landscape")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SingleSocialLink(name: String, handle: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SurfaceNavy,
        border = BorderStroke(0.5.dp, CardNavy),
        modifier = Modifier.clickable { /* Handle Nav */ }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "$name ($handle)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun CampaignOutcomeRow(campaign: String, stat: String, views: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(campaign, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = LightSageGlow.copy(alpha = 0.12f)
            ) {
                Text(stat, fontSize = 10.sp, color = LightSageGlow, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(views, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = SurfaceNavy)
    }
}

@Composable
fun PlatformProgressRow(platform: String, ratio: Float, metricLabel: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(platform, color = TextPrimary, fontSize = 13.sp)
            Text(metricLabel, color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier.fillMaxWidth(),
            color = ElectricCyan,
            trackColor = CardNavy
        )
    }
}

@Composable
fun RowScope.DummyPortfolioItem(color1: Color, color2: Color, label: String) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(110.dp)
            .background(Brush.linearGradient(listOf(color1, color2)), RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            style = TextStyle(
                shadow = Shadow(color = Color.Black, blurRadius = 3f)
            )
        )
    }
}

// --- SCREEN 5: IN-APP MEESAGES (SCREEN CHAT LIST) ---
@Composable
fun ChatListScreen(viewModel: CreatorHubViewModel) {
    val sessions by viewModel.chatSessions.collectAsStateWithLifecycle()
    var activeCategory by remember { mutableStateOf("All") } // "All", "Unread", "Archived"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Messages Header
        Text(
            text = "Messages",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        // Filter Categories Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val filters = listOf("All", "Unread", "Archived")
            filters.forEach { filter ->
                val isSel = activeCategory == filter
                Surface(
                    modifier = Modifier.clickable { activeCategory = filter },
                    color = if (isSel) ElectricCyan else SurfaceNavy,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSel) Color.White else TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (sessions.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No conversations yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sessions) { session ->
                    ChatSessionItemRow(session) {
                        viewModel.selectChatSession(session.id)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatSessionItemRow(session: ChatSessionEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("chat_session_${session.id}"),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Initial icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(CardNavy, CircleShape)
                    .border(1.2.dp, ElectricCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    session.partnerName.take(1),
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        session.partnerName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Active Now",
                        color = LightSageGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    session.lastMessage,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// --- SCREEN 5a DETAIL: ACTIVE CHAT SCREEN ---
@Composable
fun ChatDetailScreen(viewModel: CreatorHubViewModel) {
    val activeId by viewModel.activeChatSessionId.collectAsStateWithLifecycle()
    val chatSessions by viewModel.chatSessions.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    var inputMessage by remember { mutableStateOf("") }

    val activeSession = chatSessions.find { it.id == activeId }
    if (activeSession == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No Active Chat selected", color = TextSecondary)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceNavy)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo("messages") }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(CardNavy, CircleShape)
                    .border(1.dp, ElectricCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(activeSession.partnerName.take(1), color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    activeSession.partnerName,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    "Verified Partner Campaign",
                    color = ElectricCyan,
                    fontSize = 11.sp
                )
            }
        }

        // Messages Feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { msg ->
                val isCreator = msg.sender == "creator"
                val align = if (isCreator) Alignment.End else Alignment.Start
                val backColor = if (isCreator) ElectricCyan else SurfaceNavy
                val textColor = if (isCreator) Color.White else TextPrimary
                val shapeRight = if (isCreator) RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = align
                ) {
                    Surface(
                        color = backColor,
                        shape = shapeRight,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.messageText,
                            color = textColor,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Input bar
        Surface(
            color = SurfaceNavy,
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillPaddingAndNavigation()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("Write your counter pitch...", color = AppGray) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .testTag("chat_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardNavy,
                        unfocusedContainerColor = CardNavy,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendMessageToActiveChat(inputMessage)
                            inputMessage = ""
                        }
                    },
                    modifier = Modifier
                        .background(ElectricCyan, CircleShape)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = CosmicNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun Modifier.fillPaddingAndNavigation() = this.fillMaxWidth().navigationBarsPadding()

// --- SCREEN 6: DEALS & PAYMENT MANAGEMENT STATUS (Matches screenshot #6 and Escrow Panel) ---
@Composable
fun DealStatusScreen(viewModel: CreatorHubViewModel) {
    val deals by viewModel.activeDeals.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Deal Management",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                "Track secure escrows & active work contracts",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        if (deals.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No active escrow flows yet. Go to Feed to apply!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(deals) { deal ->
                    DealProgressItemCard(deal = deal) {
                        viewModel.advanceDealStatus(deal)
                    }
                }
            }
        }
    }
}

@Composable
fun DealProgressItemCard(deal: DealEntity, onAdvanceClick: () -> Unit) {
    val steps = listOf("Applied", "Accepted", "Work Submitted", "Under Review", "Completed", "Payment Released")
    val currentIndex = steps.indexOf(deal.status).coerceAtLeast(0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("deal_card_${deal.id}"),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.2.dp, NeonPurple.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header: Title + Brand + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        deal.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "by ${deal.brandName}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        deal.dealAmount,
                        color = LightSageGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (deal.status) {
                            "Payment Released" -> LightSageGlow.copy(alpha = 0.15f)
                            else -> NeonPurple.copy(alpha = 0.20f)
                        }
                    ) {
                        Text(
                            text = deal.status,
                            color = when (deal.status) {
                                "Payment Released" -> LightSageGlow
                                else -> ElectricCyan
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Escrow details
            Text(
                "CREATORHUB ESCROW PAYMENT STATUS",
                color = ElectricCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Key steps matching flow chart #7
            // Step 1: Brand Adds Funds (In Escrow) -> Applied/Accepted
            // Step 2: Creator Does the Work -> Work Submitted
            // Step 3: Brand Reviews / Approves -> Under Review / Completed
            // Step 4: Released -> Payment Released
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EscrowFlowStepRow(
                    stepLabel = "1. Brand Adds Funds (Escrow Verified)",
                    isCompleted = currentIndex >= 1,
                    isActive = currentIndex == 0
                )
                EscrowFlowStepRow(
                    stepLabel = "2. Creator Does Work (Submits Draft)",
                    isCompleted = currentIndex >= 2,
                    isActive = currentIndex == 1
                )
                EscrowFlowStepRow(
                    stepLabel = "3. Campaign Reviews & Approvals",
                    isCompleted = currentIndex >= 4,
                    isActive = currentIndex == 2 || currentIndex == 3
                )
                EscrowFlowStepRow(
                    stepLabel = "4. Safe Payment Released to Wallet",
                    isCompleted = currentIndex >= 5,
                    isActive = currentIndex == 4
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Advance status simulation button for beautiful demonstration
            if (deal.status != "Payment Released") {
                Button(
                    onClick = onAdvanceClick,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    val prompt = when (deal.status) {
                        "Applied" -> "Verify & Request Escrow Funds Start"
                        "Accepted" -> "Submit Work Draft"
                        "Work Submitted" -> "Trigger Brand Campaign Review"
                        "Under Review" -> "Approve Work & Finalise Contract"
                        "Completed" -> "Release Secure Funds to Creator Wallet"
                        else -> "Advance Next Step"
                    }
                    Text(text = prompt, color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Surface(
                    color = LightSageGlow.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, LightSageGlow.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Paid", tint = LightSageGlow)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Safe Deal Completed. Funds Released successfully!",
                            color = LightSageGlow,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EscrowFlowStepRow(stepLabel: String, isCompleted: Boolean, isActive: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        val bubbleColor = if (isCompleted) LightSageGlow else if (isActive) ElectricCyan else AppGray.copy(alpha = 0.3f)
        val iconVector = if (isCompleted) Icons.Default.Check else if (isActive) Icons.Default.Cached else Icons.Default.RadioButtonUnchecked

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(bubbleColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = if (isCompleted || isActive) Color.White else AppGray,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stepLabel,
            color = if (isCompleted) TextPrimary else if (isActive) ElectricCyan else TextSecondary,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
        )
    }
}

// --- SCREEN 7: WEBPAGE FORM TO SUBMIT NEW OPPORTUNITY (Matches + Create) ---
@Composable
fun CreateOpportunityScreen(viewModel: CreatorHubViewModel) {
    var title by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf("") }
    var budgetRange by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Brand Deal") } // "Brand Deal", "Affiliate", "Collab"
    var platform by remember { mutableStateOf("YouTube") } // "YouTube", "Instagram", etc.
    var requirements by remember { mutableStateOf("") }
    var aboutCampaign by remember { mutableStateOf("") }

    val platformOptions = listOf("YouTube", "Instagram", "Canva Pro", "TikTok")
    val typeOptions = listOf("Brand Deal", "Affiliate", "Collab")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        Text(
            text = "Post an Opportunity",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Campaign Title", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_opportunity_title")
                )
            }

            item {
                OutlinedTextField(
                    value = brandName,
                    onValueChange = { brandName = it },
                    label = { Text("Brand / Creator Name", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_opportunity_brand")
                )
            }

            item {
                OutlinedTextField(
                    value = budgetRange,
                    onValueChange = { budgetRange = it },
                    label = { Text("Escrow Budget (e.g. ₹40,000)", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_opportunity_budget")
                )
            }

            // Type Row Selector
            item {
                Column {
                    Text("Campaign Type", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        typeOptions.forEach { option ->
                            val isSel = type == option
                            Surface(
                                modifier = Modifier.clickable { type = option },
                                color = if (isSel) ElectricCyan else SurfaceNavy,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSel) Color.White else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Platform Row Selector
            item {
                Column {
                    Text("Target Platform", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        platformOptions.forEach { option ->
                            val isSel = platform == option
                            Surface(
                                modifier = Modifier.clickable { platform = option },
                                color = if (isSel) ElectricCyan else SurfaceNavy,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSel) Color.White else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = aboutCampaign,
                    onValueChange = { aboutCampaign = it },
                    label = { Text("About Campaign", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = requirements,
                    onValueChange = { requirements = it },
                    label = { Text("Requirements (One per line)", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        // Action submit bar
        Button(
            onClick = {
                if (title.isNotBlank() && brandName.isNotBlank() && budgetRange.isNotBlank()) {
                    val formattedReqs = requirements.split("\n").filter { it.isNotBlank() }.joinToString("\n") { "• $it" }
                    viewModel.createCustomOpportunity(
                        title = title,
                        brandName = brandName,
                        budgetRange = budgetRange,
                        type = type,
                        platform = platform,
                        requirements = formattedReqs,
                        aboutCampaign = aboutCampaign
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
                .testTag("submit_opportunity_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Publish Campaign to CreatorHub",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// --- SCREEN 8: LEADERBOARD SCREEN ---
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
        // Top Header section (Goal 6, section 1)
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

data class LeaderboardItem(
    val rank: Int,
    val name: String,
    val handle: String,
    val metricValue: String,
    val subMetric: String,
    val isVerified: Boolean
)

// --- FIREBASE LOGIN SCREEN ---
@Composable
fun LoginScreen(viewModel: CreatorHubViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.clearAuthError()
        onDispose {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.2f))

        CreatorHubLogo(
            iconSize = 64.dp,
            fontSize = 32.sp,
            showWordmark = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Welcome to Creator exchange. India's closed professional network.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CosmicNavy),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CardNavy)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_login_email")
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_login_password"),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )

                if (authError != null) {
                    Text(
                        text = authError ?: "",
                        color = CoralRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { viewModel.navigateTo("forgot_password") }
                    ) {
                        Text(
                            "Forgot Password?",
                            color = BrandPurple,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (authLoading) {
                    CircularProgressIndicator(
                        color = BrandIndigo,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Button(
                        onClick = { viewModel.login(email, password) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_button")
                    ) {
                        Text("Log In", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("New to CreatorHub?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = { viewModel.navigateTo("signup") }
            ) {
                Text(
                    "Sign Up",
                    color = BrandIndigo,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.3f))
    }
}

// --- FIREBASE SIGNUP SCREEN ---
@Composable
fun SignupScreen(viewModel: CreatorHubViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.clearAuthError()
        onDispose {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.2f))

        CreatorHubLogo(
            iconSize = 64.dp,
            fontSize = 32.sp,
            showWordmark = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Create security-escrow creator deals instantly.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CosmicNavy),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CardNavy)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_signup_email")
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password (Min 6 chars)", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_signup_password"),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )

                if (authError != null) {
                    Text(
                        text = authError ?: "",
                        color = CoralRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (authLoading) {
                    CircularProgressIndicator(
                        color = BrandIndigo,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Button(
                        onClick = { viewModel.signup(email, password) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("signup_button")
                    ) {
                        Text("Create Account", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = { viewModel.navigateTo("login") }
            ) {
                Text(
                    "Log In",
                    color = BrandIndigo,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.3f))
    }
}

// --- FIREBASE FORGOT PASSWORD SCREEN ---
@Composable
fun ForgotPasswordScreen(viewModel: CreatorHubViewModel) {
    var email by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.clearAuthError()
        onDispose {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.2f))

        CreatorHubLogo(
            iconSize = 64.dp,
            fontSize = 32.sp,
            showWordmark = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Reset your professional account password securely.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CosmicNavy),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CardNavy)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_reset_email")
                )

                if (authError != null) {
                    val isSuccess = authError == "Password reset email sent successfully!"
                    Text(
                        text = authError ?: "",
                        color = if (isSuccess) LightSageGlow else CoralRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (authLoading) {
                    CircularProgressIndicator(
                        color = BrandIndigo,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Button(
                        onClick = { viewModel.forgotPassword(email) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("reset_password_button")
                    ) {
                        Text("Send Reset Link", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Back to login?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = { viewModel.navigateTo("login") }
            ) {
                Text(
                    "Log In",
                    color = BrandIndigo,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.3f))
    }
}
