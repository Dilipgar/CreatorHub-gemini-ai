package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ChatSessionEntity
import com.example.data.DealEntity
import com.example.data.MessageEntity

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
            "welcome" -> WelcomeScreen(viewModel)
            "login" -> LoginScreen(viewModel)
            "signup" -> SignupScreen(viewModel)
            "signup_creator" -> SignupCreatorScreen(viewModel)
            "signup_brand" -> SignupBrandScreen(viewModel)
            "forgot_password" -> ForgotPasswordScreen(viewModel)
            "opportunities" -> MainAppScaffold(viewModel, "opportunities") { OpportunitiesFeedScreen(viewModel) }
            "details" -> MainAppScaffold(viewModel, "opportunities") { OpportunityDetailScreen(viewModel) }
            "leaderboard" -> MainAppScaffold(viewModel, "leaderboard") { LeaderboardScreen(viewModel) }
            "affiliates" -> MainAppScaffold(viewModel, "affiliates") { AffiliateMarketplaceScreen(viewModel) }
            "affiliate_details" -> MainAppScaffold(viewModel, "affiliates") { AffiliateDetailScreen(viewModel) }
            "messages" -> MainAppScaffold(viewModel, "messages") { ChatListScreen(viewModel) }
            "chat_detail" -> MainAppScaffold(viewModel, "messages") { ChatDetailScreen(viewModel) }
            "create" -> MainAppScaffold(viewModel, "create") { CreateOpportunityScreen(viewModel) }
            "deals" -> MainAppScaffold(viewModel, "deals") { DealStatusScreen(viewModel) }
            "profile" -> MainAppScaffold(viewModel, "profile") { CreatorProfileScreen(viewModel) }
            "notifications" -> MainAppScaffold(viewModel, "opportunities") { NotificationCenterScreen(viewModel) }
            
            // Brand-specific screens
            "campaign_management" -> MainAppScaffold(viewModel, "campaign_management") { CampaignManagementScreen(viewModel) }
            "applicant_management" -> MainAppScaffold(viewModel, "applicant_management") { ApplicantManagementScreen(viewModel) }
            "creator_discovery" -> MainAppScaffold(viewModel, "creator_discovery") { CreatorDiscoveryScreen(viewModel) }
            "brand_profile" -> MainAppScaffold(viewModel, "brand_profile") { BrandProfileScreen(viewModel) }
        }
    }
}

@Composable
fun MainAppScaffold(
    viewModel: CreatorHubViewModel,
    activeTab: String,
    content: @Composable () -> Unit
) {
    val selectedRole by viewModel.selectedRole.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceNavy,
                contentColor = TextSecondary,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                if (selectedRole == "brand") {
                    NavigationBarItem(
                        selected = activeTab == "campaign_management",
                        onClick = { viewModel.navigateTo("campaign_management") },
                        icon = { Icon(Icons.Default.Campaign, contentDescription = "Campaigns") },
                        label = { Text("Campaigns", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            unselectedIconColor = AppGray,
                            unselectedTextColor = AppGray,
                            indicatorColor = CardNavy
                        ),
                        modifier = Modifier.testTag("nav_campaign_management")
                    )
                    NavigationBarItem(
                        selected = activeTab == "applicant_management",
                        onClick = { viewModel.navigateTo("applicant_management") },
                        icon = { Icon(Icons.Default.People, contentDescription = "Applicants") },
                        label = { Text("Applicants", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            unselectedIconColor = AppGray,
                            unselectedTextColor = AppGray,
                            indicatorColor = CardNavy
                        ),
                        modifier = Modifier.testTag("nav_applicant_management")
                    )
                    NavigationBarItem(
                        selected = activeTab == "creator_discovery",
                        onClick = { viewModel.navigateTo("creator_discovery") },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Discover") },
                        label = { Text("Discover", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            unselectedIconColor = AppGray,
                            unselectedTextColor = AppGray,
                            indicatorColor = CardNavy
                        ),
                        modifier = Modifier.testTag("nav_creator_discovery")
                    )
                    NavigationBarItem(
                        selected = activeTab == "brand_profile",
                        onClick = { viewModel.navigateTo("brand_profile") },
                        icon = { Icon(Icons.Default.Business, contentDescription = "Brand Profile") },
                        label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            unselectedIconColor = AppGray,
                            unselectedTextColor = AppGray,
                            indicatorColor = CardNavy
                        ),
                        modifier = Modifier.testTag("nav_brand_profile")
                    )
                } else {
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
                        selected = activeTab == "affiliates",
                        onClick = { viewModel.navigateTo("affiliates") },
                        icon = { Icon(Icons.Default.Storefront, contentDescription = "Affiliate Marketplace") },
                        label = { Text("Affiliates", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            unselectedIconColor = AppGray,
                            unselectedTextColor = AppGray,
                            indicatorColor = CardNavy
                        ),
                        modifier = Modifier.testTag("nav_affiliates")
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
            }
        },
        containerColor = DarkNavy,
        contentWindowInsets = WindowInsets.navigationBars
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

// --- SCREEN 5: IN-APP MESSAGES (SCREEN CHAT LIST) ---
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
                    .fillMaxWidth()
                    .navigationBarsPadding()
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

// --- SCREEN 6: DEALS & PAYMENT MANAGEMENT STATUS ---
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
                    Spacer(modifier = Modifier.height(4.dp))
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
