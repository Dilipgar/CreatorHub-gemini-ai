package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.UserProfile

@Composable
fun CreatorProfileScreen(viewModel: CreatorHubViewModel) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    var isEditing by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    var editName by remember(profile) { mutableStateOf(profile?.name ?: "Ankit Photographer") }
    var editHandle by remember(profile) { mutableStateOf(profile?.handle ?: "@ankitclicks") }
    var editLocation by remember(profile) { mutableStateOf(profile?.location ?: "Mumbai, IN") }
    var editBio by remember(profile) { mutableStateOf(profile?.bio ?: "I generate premium lifestyle content and social media vlogs focusing on architectural landmarks, drone landscape cinematography, travel storytelling, and luxury retreats. Dedicated to delivering high-CTR assets with zero-dispute contract compliance.") }
    var editInsta by remember(profile) { mutableStateOf(profile?.instagramFollowers ?: "125K") }
    var editYt by remember(profile) { mutableStateOf(profile?.youtubeSubscribers ?: "92K") }
    var editTwitter by remember(profile) { mutableStateOf(profile?.twitterFollowers ?: "14K") }
    
    // Custom profile sprint fields
    var editPhotoUrl by remember(profile) { mutableStateOf(profile?.profilePhoto ?: "") }
    var editInstagramUrl by remember(profile) { mutableStateOf(profile?.instagramUrl ?: "https://instagram.com/ankitclicks") }
    var editYoutubeUrl by remember(profile) { mutableStateOf(profile?.youtubeUrl ?: "https://youtube.com/c/ankitvlogs") }
    var editWebsite by remember(profile) { mutableStateOf(profile?.website ?: "https://ankitclicks.com") }
    var editCategories by remember(profile) { mutableStateOf(profile?.categories ?: "Photography, Travel, Videography") }
    var editFollowers by remember(profile) { mutableStateOf(profile?.followers ?: "231K") }
    var editPortfolioLinks by remember(profile) { mutableStateOf(profile?.portfolioLinks ?: "https://behance.net/ankitclicks, https://unsplash.com/@ankitclicks") }

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
                                twitterFollowers = editTwitter,
                                profilePhoto = editPhotoUrl,
                                instagramUrl = editInstagramUrl,
                                youtubeUrl = editYoutubeUrl,
                                website = editWebsite,
                                categories = editCategories,
                                followers = editFollowers,
                                portfolioLinks = editPortfolioLinks
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

                Text(
                    text = "Professional Portal Links & Verification",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = editPhotoUrl,
                    onValueChange = { editPhotoUrl = it },
                    label = { Text("Profile Photo URL", color = ElectricCyan) },
                    placeholder = { Text("https://example.com/photo.jpg", color = AppGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_photo")
                )

                OutlinedTextField(
                    value = editInstagramUrl,
                    onValueChange = { editInstagramUrl = it },
                    label = { Text("Instagram URL", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_instagram_url")
                )

                OutlinedTextField(
                    value = editYoutubeUrl,
                    onValueChange = { editYoutubeUrl = it },
                    label = { Text("YouTube URL", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_youtube_url")
                )

                OutlinedTextField(
                    value = editWebsite,
                    onValueChange = { editWebsite = it },
                    label = { Text("Personal Website", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_website")
                )

                OutlinedTextField(
                    value = editCategories,
                    onValueChange = { editCategories = it },
                    label = { Text("Creator Categories (comma split)", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_categories")
                )

                OutlinedTextField(
                    value = editFollowers,
                    onValueChange = { editFollowers = it },
                    label = { Text("Aggregate Followers count (e.g. 250K)", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_followers_count")
                )

                OutlinedTextField(
                    value = editPortfolioLinks,
                    onValueChange = { editPortfolioLinks = it },
                    label = { Text("Portfolio Showcase Links (comma split)", color = ElectricCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_portfolio_links"),
                    singleLine = false,
                    maxLines = 3
                )

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
                            twitterFollowers = editTwitter,
                            profilePhoto = editPhotoUrl,
                            instagramUrl = editInstagramUrl,
                            youtubeUrl = editYoutubeUrl,
                            website = editWebsite,
                            categories = editCategories,
                            followers = editFollowers,
                            portfolioLinks = editPortfolioLinks
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
                                    if (editPhotoUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = editPhotoUrl,
                                            contentDescription = "Creator profile picture",
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop,
                                            error = painterResource(id = R.drawable.img_creator_avatar)
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_creator_avatar),
                                            contentDescription = "Creator profile picture",
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
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
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        editCategories.split(",").map { it.trim() }.filter { it.isNotEmpty() }.take(3).forEach { cat ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = BrandIndigo.copy(alpha = 0.1f)
                                            ) {
                                                Text(
                                                    cat,
                                                    color = BrandPurple,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
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

                            // Social Media Links (Goal 4 & Click action #5)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Quick Handles", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    SingleSocialLink(name = "Instagram", handle = editFollowers.takeIf { it.isNotBlank() } ?: editInsta, color = Color(0xFFE1306C), onClick = {
                                        if (editInstagramUrl.isNotBlank()) {
                                            try { uriHandler.openUri(editInstagramUrl) } catch (e: Exception) { e.printStackTrace() }
                                        }
                                    })
                                    SingleSocialLink(name = "YouTube", handle = editYt, color = Color(0xFFFF0000), onClick = {
                                        if (editYoutubeUrl.isNotBlank()) {
                                            try { uriHandler.openUri(editYoutubeUrl) } catch (e: Exception) { e.printStackTrace() }
                                        }
                                    })
                                    SingleSocialLink(name = "Web", handle = "Website", color = BrandIndigo, onClick = {
                                        if (editWebsite.isNotBlank()) {
                                            try { uriHandler.openUri(editWebsite) } catch (e: Exception) { e.printStackTrace() }
                                        }
                                    })
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

                // Portfolio Showcase Section (Sprint requirement #3)
                item {
                    Column {
                        Text(
                            "Verified Portfolio Showcases",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (editPortfolioLinks.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                editPortfolioLinks.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { link ->
                                    val domain = link.substringAfter("://").substringBefore("/")
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = CosmicNavy,
                                        border = BorderStroke(1.dp, CardNavy),
                                        modifier = Modifier.clickable {
                                            try {
                                                val targetUri = if (!link.startsWith("http://") && !link.startsWith("https://")) {
                                                    "https://$link"
                                                } else {
                                                    link
                                                }
                                                uriHandler.openUri(targetUri)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Launch,
                                                contentDescription = "Portfolio Link",
                                                tint = BrandIndigo,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = domain.ifEmpty { "Portfolio" },
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandIndigo
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                "No verified portfolios linked yet. Tab edit profile to showcase your best creations.",
                                color = AppGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
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
fun SingleSocialLink(name: String, handle: String, color: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "$name: $handle", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CampaignOutcomeRow(campaign: String, stat: String, views: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(campaign, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(views, fontSize = 11.sp, color = AppGray)
        }
        Text(stat, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightSageGlow)
    }
}

@Composable
fun PlatformProgressRow(platform: String, ratio: Float, metricLabel: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(platform, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(metricLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { ratio },
            color = ElectricCyan,
            trackColor = CardNavy,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
        )
    }
}

@Composable
fun DummyPortfolioItem(color1: Color, color2: Color, label: String) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(color1, color2)))
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .background(Color.White.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play preview", tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}
