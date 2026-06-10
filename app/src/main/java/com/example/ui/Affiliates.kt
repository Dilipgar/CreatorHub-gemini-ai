package com.example.ui

import androidx.compose.animation.*
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AffiliateEarningEntity
import com.example.data.AffiliateOfferEntity
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AffiliateMarketplaceScreen(viewModel: CreatorHubViewModel) {
    val offers by viewModel.filteredAffiliateOffers.collectAsStateWithLifecycle()
    val earnings by viewModel.affiliateEarnings.collectAsStateWithLifecycle()
    val searchQuery by viewModel.affiliateSearchQuery.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.affiliateCategoryFilter.collectAsStateWithLifecycle()

    val categories = listOf("All", "SaaS & Hosting", "Design & Creative", "E-commerce & Retail", "Education & Self-Care")

    // Reactive Calculations for Earnings tracking structure
    val totalClicks = earnings.sumOf { it.clicksCount }
    val totalConversions = earnings.sumOf { it.conversionsCount }
    val totalSales = earnings.sumOf { it.totalSales }
    val totalEarnings = earnings.sumOf { it.earningsAmount }
    val conversionRate = if (totalClicks > 0) {
        (totalConversions.toDouble() / totalClicks.toDouble()) * 100
    } else {
        0.0
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Title Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Affiliate Marketplace",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Earn recurring commission with premium brands",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SurfaceNavy, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Marketplace icon",
                        tint = ElectricCyan
                    )
                }
            }
        }

        // Real-time Earnings Tracking Dashboard Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f)), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = "Stats",
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Live Performance Dashboard",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Surface(
                            color = LightSageGlow.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "EST. EARNINGS",
                                color = LightSageGlow,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Income",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                text = currencyFormatter.format(totalEarnings).substringBefore("."),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = LightSageGlow,
                                fontSize = 28.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Sales Generated",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                text = currencyFormatter.format(totalSales).substringBefore("."),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = CardNavy, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Clicks", style = MaterialTheme.typography.labelSmall, color = AppGray)
                            Text("$totalClicks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Conversions", style = MaterialTheme.typography.labelSmall, color = AppGray)
                            Text("$totalConversions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Click-through Rate", style = MaterialTheme.typography.labelSmall, color = AppGray)
                            Text(String.format("%.2f%%", conversionRate), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        }
                    }
                }
            }
        }

        // Search Bar Section
        item {
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.setAffiliateSearchQuery(it) },
                placeholder = { Text("Search affiliate contracts and companies...", color = AppGray) },
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
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("aff_search_bar")
            )
        }

        // Horizontal Category Filters LazyRow
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == categoryFilter
                    val backColor = if (isSelected) ElectricCyan else SurfaceNavy
                    val textColor = if (isSelected) Color.White else TextSecondary
                    val strokeColor = if (isSelected) Color.Transparent else NeonPurple.copy(alpha = 0.5f)

                    Surface(
                        modifier = Modifier.clickable { viewModel.setAffiliateCategoryFilter(cat) },
                        shape = RoundedCornerShape(20.dp),
                        color = backColor,
                        border = BorderStroke(1.dp, strokeColor)
                    ) {
                        Text(
                            text = cat,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // List Header label
        item {
            Text(
                text = "Available Referral Offers (${offers.size})",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Custom Lists of Affiliate Offers
        if (offers.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = "Empty Marketplace",
                        tint = AppGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No offers meet this criteria",
                        color = TextSecondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Try adjusting your search query or choosing another category class.",
                        color = AppGray,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            items(offers) { offer ->
                AffiliateOfferCard(
                    offer = offer,
                    earnings = earnings.find { it.offerId == offer.id },
                    onCardClick = { viewModel.selectAffiliateOffer(offer.id) },
                    onSaveToggle = { viewModel.toggleSaveAffiliateOffer(offer.id) },
                    onApplyClick = { viewModel.applyToAffiliateOffer(offer.id, offer.brandName) },
                    onSimulateClick = { viewModel.simulateClickOnAffiliate(offer.id) }
                )
            }
        }
    }
}

@Composable
fun AffiliateOfferCard(
    offer: AffiliateOfferEntity,
    earnings: AffiliateEarningEntity?,
    onCardClick: () -> Unit,
    onSaveToggle: () -> Unit,
    onApplyClick: () -> Unit,
    onSimulateClick: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("aff_offer_card_${offer.id}"),
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
                // Name block
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CardNavy, CircleShape)
                            .border(1.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = offer.brandName.take(1),
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = offer.brandName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = NeonPurple.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = offer.category,
                                color = NeonPurple,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Heart Icon
                IconButton(onClick = onSaveToggle) {
                    Icon(
                        imageVector = if (offer.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Save Offer icon",
                        tint = if (offer.isSaved) CoralRed else AppGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = offer.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rate details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Growth",
                    tint = LightSageGlow,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = offer.commissionRate,
                    color = LightSageGlow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = NeonPurple.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            if (offer.isApplied) {
                // Applied Details and Traffic Simulation Widget
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Active",
                                tint = LightSageGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Linked Tracker Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = LightSageGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Mini statistics if clicks exist
                        if (earnings != null) {
                            Text(
                                text = "Clicks: ${earnings.clicksCount}  |  Earnings: ${currencyFormatter.format(earnings.earningsAmount).substringBefore(".")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = offer.affiliateLink,
                        fontSize = 10.sp,
                        color = ElectricCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulated Traffic Click Button! Great for visual play testing
                    Button(
                        onClick = onSimulateClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = "Simulate Traffic icon",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Simulate Link Click (8% Conv. Chance)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "Unlock referral tracking links instantly upon request approval.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppGray,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCardClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "info",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Details", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }

                if (!offer.isApplied) {
                    Button(
                        onClick = onApplyClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Apply & Get Link", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Check, contentDescription = "check", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                } else {
                    OutlinedButton(
                        onClick = onCardClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LightSageGlow),
                        border = BorderStroke(1.dp, LightSageGlow.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = "Active graph",
                            modifier = Modifier.size(16.dp),
                            tint = LightSageGlow
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Active Track Link", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = LightSageGlow)
                    }
                }
            }
        }
    }
}

@Composable
fun AffiliateDetailScreen(viewModel: CreatorHubViewModel) {
    val offerFlow by viewModel.selectedAffiliateOffer.collectAsStateWithLifecycle()
    val earningsList by viewModel.affiliateEarnings.collectAsStateWithLifecycle()
    
    val clipManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    if (offerFlow == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricCyan)
        }
        return
    }

    val offer = offerFlow!!
    val earnings = earningsList.find { it.offerId == offer.id }
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Back toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo("affiliates") },
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
                "Offer Details",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricCyan.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = offer.category,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(onClick = { viewModel.toggleSaveAffiliateOffer(offer.id) }) {
                            Icon(
                                imageVector = if (offer.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite Toggle",
                                tint = if (offer.isSaved) CoralRed else AppGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = offer.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Published by ${offer.brandName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = CardNavy, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Commission Structure", style = MaterialTheme.typography.labelSmall, color = AppGray)
                            Text(
                                text = offer.commissionRate,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LightSageGlow
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Payout Schedule", style = MaterialTheme.typography.labelSmall, color = AppGray)
                            Text(
                                text = offer.payoutInfo.split(",").firstOrNull() ?: "Monthly",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Affiliate Details Segment
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                border = BorderStroke(1.dp, CardNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "About the Referral Campaign",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = offer.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        "Payout Specifications",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = offer.payoutInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Original Product Resource",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = offer.productUrl,
                        fontSize = 11.sp,
                        color = ElectricCyan,
                        modifier = Modifier.clickable {
                            try {
                                val target = if (!offer.productUrl.startsWith("http://") && !offer.productUrl.startsWith("https://")) {
                                    "https://${offer.productUrl}"
                                } else {
                                    offer.productUrl
                                }
                                uriHandler.openUri(target)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )
                }
            }

            // Interactive Tracking Segment
            if (offer.isApplied) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                    border = BorderStroke(1.dp, LightSageGlow.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Your Contract Tracking Link",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = LightSageGlow.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Active status link",
                                        tint = LightSageGlow,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "ACTIVE",
                                        color = LightSageGlow,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Share this exact link. Purchases or signups completed through this address register as conversions allocated directly as your commission payload.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, CardNavy, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = offer.affiliateLink,
                                fontSize = 11.sp,
                                color = ElectricCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(
                                onClick = { clipManager.setText(AnnotatedString(offer.affiliateLink)) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy text link",
                                    tint = AppGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = CardNavy, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Earnings Simulator Widget inside Detail page
                        Text(
                            "Referral Traffic Simulator",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Generate live clicks mock-triggers. This acts as user traffic tapping your link, generating random conversions and instantly updating the local room database record statistics below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppGray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Clicks", style = MaterialTheme.typography.labelSmall, color = AppGray)
                                Text("${earnings?.clicksCount ?: 0}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text("Conversions", style = MaterialTheme.typography.labelSmall, color = AppGray)
                                Text("${earnings?.conversionsCount ?: 0}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Earned Royalties", style = MaterialTheme.typography.labelSmall, color = AppGray)
                                Text(
                                    currencyFormatter.format(earnings?.earningsAmount ?: 0.0).substringBefore("."),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LightSageGlow,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.simulateClickOnAffiliate(offer.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Run Click mock")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Trigger Custom Click Transaction", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Apply contract panel
        if (!offer.isApplied) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                color = SurfaceNavy
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Payout Yield", style = MaterialTheme.typography.labelSmall, color = AppGray)
                        Text(offer.commissionRate, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = LightSageGlow)
                    }

                    Button(
                        onClick = { viewModel.applyToAffiliateOffer(offer.id, offer.brandName) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Apply & Generate Tracker", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Storefront, contentDescription = "store", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
