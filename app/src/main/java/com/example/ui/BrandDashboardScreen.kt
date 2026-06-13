package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CampaignManagementScreen(viewModel: CreatorHubViewModel) {
    val opportunities by viewModel.allOpportunities.collectAsStateWithLifecycle()
    val brandProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val companyName = brandProfile?.name ?: "Reliance Jio"

    // brand list
    val brandCampaigns = opportunities.filter { it.brandName.equals(companyName, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Campaign Portal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                     text = "Manage your escrow-backed campaigns & listings",
                     style = MaterialTheme.typography.bodySmall,
                     color = AppGray
                )
            }
            
            FloatingActionButton(
                onClick = { viewModel.navigateTo("create") },
                containerColor = ElectricCyan,
                contentColor = Color.White,
                modifier = Modifier.size(48.dp).testTag("brand_fab_create_campaign")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Campaign")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (brandCampaigns.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = AppGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Kampaigns Listed Yet", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Post continuous briefs directly into feeds.", style = MaterialTheme.typography.bodySmall, color = AppGray)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(brandCampaigns) { cam ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("campaign_item_${cam.id}"),
                        colors = CardDefaults.cardColors(containerColor = CosmicNavy),
                        border = BorderStroke(1.dp, CardNavy)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cam.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = LightSageGlow.copy(alpha = 0.15f)
                                ) {
                                    Text("Active Listings", color = LightSageGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Channel type: ${cam.platform} • Base Budget: ${cam.budgetRange}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.navigateTo("applicant_management") },
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                border = BorderStroke(1.dp, CardNavy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(40.dp)
                            ) {
                                Text("Inspect Applicant Pipeline", color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class MockApplicant(
    val id: Int,
    val name: String,
    val specialty: String,
    val platform: String,
    val metrics: String,
    val trustScore: String,
    val proposedBudget: String
)

@Composable
fun ApplicantManagementScreen(viewModel: CreatorHubViewModel) {
    var approvedId by remember { mutableStateOf<Int?>(null) }
    
    val applicants = listOf(
        MockApplicant(1, "Ankit clicks", "Photography", "Instagram", "125K Reach", "Trust: 96%", "₹50,000"),
        MockApplicant(2, "Rohan Sharma", "Tech", "YouTube", "240k Subs", "Trust: 98%", "₹45,000"),
        MockApplicant(3, "Sunita Sen", "Travel Vlog", "Instagram", "89k Reach", "Trust: 95%", "₹35,000")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(16.dp)
    ) {
        Text(
            text = "Applicant Pipeline",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Verify applicant statistics and complete secure escrow contracts",
            style = MaterialTheme.typography.bodyMedium,
            color = AppGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(applicants) { applicant ->
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("applicant_card_${applicant.id}"),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavy),
                    border = BorderStroke(1.dp, CardNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(ElectricCyan.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(applicant.name.take(1), color = ElectricCyan, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(applicant.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("${applicant.specialty} • ${applicant.platform}", style = MaterialTheme.typography.labelSmall, color = AppGray, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(applicant.trustScore, color = NeonPurple, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Performance: ${applicant.metrics}", style = MaterialTheme.typography.bodySmall, color = AppGray)
                            Text("Proposal: ${applicant.proposedBudget}", style = MaterialTheme.typography.bodySmall, color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (approvedId == applicant.id) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFECFDF5), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓ Escrow Funded Successfully!", color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { approvedId = applicant.id },
                                    colors = ButtonDefaults.buttonColors(containerColor = LightSageGlow),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(42.dp).testTag("accept_applicant_${applicant.id}")
                                ) {
                                    Text("Fund Escrow & Accept", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                
                                Button(
                                    onClick = { viewModel.navigateTo("messages") },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                    border = BorderStroke(1.dp, CardNavy),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(42.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "Chat", tint = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class MockCreator(
    val id: Int,
    val name: String,
    val category: String,
    val platform: String,
    val metrics: String,
    val trust: String,
    val baseRate: String
)

@Composable
fun CreatorDiscoveryScreen(viewModel: CreatorHubViewModel) {
    var searchVal by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var offeredId by remember { mutableStateOf<Int?>(null) }

    val categories = listOf("All", "Tech", "Travel", "Lifestyle", "Finance")

    val creators = listOf(
        MockCreator(1, "Rohan Sharma", "Tech", "YouTube", "240k Sub", "Trust: 98%", "₹40,000/vid"),
        MockCreator(2, "Sunita Sen", "Travel", "Instagram", "89k Follow", "Trust: 96%", "₹35,000/post"),
        MockCreator(3, "Priya Mehra", "Lifestyle", "Instagram", "150k Follow", "Trust: 94%", "₹38,000/post"),
        MockCreator(4, "Karan Malhotra", "Finance", "YouTube", "120k Sub", "Trust: 97%", "₹45,000/vid")
    )

    val filteredCreators = creators.filter {
        (selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)) &&
        it.name.contains(searchVal, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(16.dp)
    ) {
        Text(
            text = "Creator Directory",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Search and filters of verified content creators across multiple niches.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search text field
        OutlinedTextField(
            value = searchVal,
            onValueChange = { searchVal = it },
            label = { Text("Search by name / niche...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = AppGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedCategory == cat) ElectricCyan else SurfaceNavy)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(cat, color = if (selectedCategory == cat) Color.White else AppGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCreators) { creator ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavy),
                    border = BorderStroke(1.dp, CardNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(ElectricCyan.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = ElectricCyan)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(creator.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("${creator.category} • ${creator.platform}", style = MaterialTheme.typography.labelSmall, color = AppGray, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(creator.trust, color = NeonPurple, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Network: ${creator.metrics}", style = MaterialTheme.typography.bodySmall, color = AppGray)
                            Text(creator.baseRate, style = MaterialTheme.typography.bodySmall, color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (offeredId == creator.id) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFECFDF5), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓ Direct Offer Sent!", color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { offeredId = creator.id },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("Send Brand Offer", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateOpportunityScreen(viewModel: CreatorHubViewModel) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Remote, IN") }
    var budgetRange by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("Instagram") }
    var type by remember { mutableStateOf("Brand Deal") }
    var durationText by remember { mutableStateOf("Fixed-Rate Contract") }
    var commissionRate by remember { mutableStateOf("") }
    var aboutCampaign by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Photography") }
    var difficultyLevel by remember { mutableStateOf("Intermediate Partner") }

    val platformTypes = listOf("Instagram", "YouTube", "Twitter", "Canva Pro", "Creative")
    val dealTypes = listOf("Brand Deal", "Affiliate", "Collab", "Contest")

    val brandProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val companyName = brandProfile?.name ?: "Reliance Jio"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create Opportunity Brief",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Escrow funds are deposited into verified contracts upon applicant match confirmation.",
            style = MaterialTheme.typography.bodySmall,
            color = AppGray
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Campaign Title") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = AppGray
            ),
            modifier = Modifier.fillMaxWidth().testTag("create_brief_title")
        )

        OutlinedTextField(
            value = budgetRange,
            onValueChange = { budgetRange = it },
            label = { Text("Base Budget (e.g. ₹40,000)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = AppGray
            ),
            modifier = Modifier.fillMaxWidth().testTag("create_brief_budget")
        )

        OutlinedTextField(
            value = commissionRate,
            onValueChange = { commissionRate = it },
            label = { Text("Commission Rate (e.g. 12% Per Sale - Optional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = AppGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Primary Category Niche (e.g. Travel, SaaS)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = AppGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = difficultyLevel,
            onValueChange = { difficultyLevel = it },
            label = { Text("Required Creator Experience (e.g. Intermediate Partner)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = AppGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = aboutCampaign,
            onValueChange = { aboutCampaign = it },
            label = { Text("Brief Description of Goals") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = AppGray
            ),
            singleLine = false,
            modifier = Modifier.fillMaxWidth().height(100.dp).testTag("create_brief_about")
        )

        OutlinedTextField(
            value = requirements,
            onValueChange = { requirements = it },
            label = { Text("Work Requirements (e.g. 1 Video post with compliance tracking)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = AppGray
            ),
            singleLine = false,
            modifier = Modifier.fillMaxWidth().height(100.dp).testTag("create_brief_requirements")
        )

        // Dropdown placeholders
        Text("Target Platform Distribution", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(platformTypes) { typeItem ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (platform == typeItem) ElectricCyan else SurfaceNavy)
                        .clickable { platform = typeItem }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(typeItem, color = if (platform == typeItem) Color.White else AppGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("Agreement Classification Model", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dealTypes) { typeItem ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (type == typeItem) ElectricCyan else SurfaceNavy)
                        .clickable { type = typeItem }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(typeItem, color = if (type == typeItem) Color.White else AppGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && budgetRange.isNotBlank()) {
                     viewModel.createCustomOpportunity(
                         title = title,
                         brandName = companyName,
                         budgetRange = budgetRange,
                         type = type,
                         platform = platform,
                         requirements = requirements,
                         aboutCampaign = aboutCampaign
                     )
                     // back to campaigns
                     viewModel.navigateTo("campaign_management")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("create_brief_submit")
        ) {
            Text("Publish & Fund Escrow Portal", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
