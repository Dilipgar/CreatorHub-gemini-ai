package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val desc: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(viewModel: CreatorHubViewModel) {
    var step by remember { mutableStateOf(0) }
    val steps = listOf(
        OnboardingStep(
            title = "Welcome to CreatorHub",
            subtitle = "India's Verified Professional Network",
            desc = "Connect securely with verified global brands, access premium campaigns, and safeguard your workflow within a closed, elite network.",
            icon = Icons.Default.Handshake
        ),
        OnboardingStep(
            title = "For Elite Creators",
            subtitle = "Guaranteed Escrow Payouts",
            desc = "Apply to high-funded briefs, secure contract amounts safely in escrow before you work, and unlock instant compliance payouts.",
            icon = Icons.Default.Verified
        ),
        OnboardingStep(
            title = "For High-Growth Brands",
            subtitle = "Pristine Creator Discovery",
            desc = "Post detailed campaign requirements, screen top-tier creator applicants, verify compliance scores, and manage contracts effortlessly.",
            icon = Icons.Default.Business
        )
    )

    val currentStep = steps[step]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper section: Skip option and logo
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CreatorHubLogo(
                iconSize = 32.dp,
                fontSize = 18.sp,
                showWordmark = true
            )
            
            TextButton(
                onClick = { viewModel.setOnboardingSeen() }
            ) {
                Text(
                    text = "Skip",
                    color = AppGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Center section: Visual illustration Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 32.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, CardNavy)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Feature Icon within a gorgeous glow background
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(ElectricCyan.copy(alpha = 0.15f), Color.Transparent)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White, CircleShape)
                            .border(1.5.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentStep.icon,
                            contentDescription = currentStep.title,
                            tint = ElectricCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = currentStep.subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentStep.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentStep.desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        // Lower section: Pagination dots and active controls
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Slider Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == step) 16.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (index == step) ElectricCyan else AppGray.copy(alpha = 0.4f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step-- },
                        border = BorderStroke(1.5.dp, CardNavy),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        Text(
                            text = "Back",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step < steps.size - 1) {
                            step++
                        } else {
                            viewModel.setOnboardingSeen()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(if (step > 0) 1.5f else 1f)
                        .height(54.dp)
                        .testTag("get_started_button")
                ) {
                    Text(
                        text = if (step == steps.size - 1) "Get Started" else "Next",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(viewModel: CreatorHubViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        CreatorHubLogo(iconSize = 56.dp, fontSize = 28.sp, showWordmark = true)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Welcome to CreatorHub",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Choose your role to customize your specialized journey",
            style = MaterialTheme.typography.bodyMedium,
            color = AppGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        // Creator Selector Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo("signup_creator") }
                .testTag("continue_as_creator_card"),
            colors = CardDefaults.cardColors(containerColor = CosmicNavy),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.5.dp, ElectricCyan.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(ElectricCyan.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Creator Icon", tint = ElectricCyan, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Continue as Creator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Access deals, collaborate on briefs, and secure escrow earnings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppGray
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Brand Selector Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo("signup_brand") }
                .testTag("continue_as_brand_card"),
            colors = CardDefaults.cardColors(containerColor = CosmicNavy),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.5.dp, NeonPurple.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(NeonPurple.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Business, contentDescription = "Brand Icon", tint = NeonPurple, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Continue as Brand / Partner",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Post campaign briefs, direct escrow milestones, and screen top-tier creator applicants.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already registered?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = { viewModel.navigateTo("login") }
            ) {
                Text(
                     "Sign In",
                     color = ElectricCyan,
                     fontWeight = FontWeight.Bold,
                     style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
