package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
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
import com.example.data.UserProfile

@Composable
fun BrandProfileScreen(viewModel: CreatorHubViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    var isEditMode by remember { mutableStateOf(false) }

    // local edit controller
    var companyName by remember { mutableStateOf(userProfile?.name ?: "Reliance Jio") }
    var industry by remember { mutableStateOf(userProfile?.industry ?: "Telecom / SaaS") }
    var website by remember { mutableStateOf(userProfile?.website ?: "jio.com") }
    var bio by remember { mutableStateOf(userProfile?.bio ?: "We connect India. Looking for outstanding digital creators to lead premium 5G reviews and SaaS lifestyle videos.") }
    var location by remember { mutableStateOf(userProfile?.location ?: "Mumbai, MH") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        // Brand Header Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CosmicNavy),
            border = BorderStroke(1.dp, CardNavy)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(NeonPurple.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(44.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isEditMode) "Edit Brand Profile" else companyName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "$industry • $location",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppGray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CosmicNavy),
            border = BorderStroke(1.dp, CardNavy)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Verification Info", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)

                if (isEditMode) {
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Company Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = AppGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = industry,
                        onValueChange = { industry = it },
                        label = { Text("Industry") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = AppGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = website,
                        onValueChange = { website = it },
                        label = { Text("Website") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = AppGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = AppGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio / Description") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = AppGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val nextProfile = (userProfile ?: UserProfile(uid = "local_demo_uid", email = "brand@brand.com")).copy(
                                name = companyName,
                                industry = industry,
                                website = website,
                                bio = bio,
                                location = location,
                                role = "brand"
                            )
                            viewModel.userProfile.value = nextProfile
                            viewModel.saveProfileLocally(nextProfile)
                            isEditMode = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Profile Changes", color = Color.White)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Corporate Website", style = MaterialTheme.typography.bodyMedium, color = AppGray)
                        Text(website, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ElectricCyan)
                    }
                    HorizontalDivider(color = CardNavy)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Agency Bio", style = MaterialTheme.typography.bodyMedium, color = AppGray, fontWeight = FontWeight.Bold)
                        Text(bio, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, lineHeight = 20.sp)
                    }
                    HorizontalDivider(color = CardNavy)

                    Button(
                        onClick = { isEditMode = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                        border = BorderStroke(1.dp, CardNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Corporate Profile", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout
        Button(
            onClick = {
                viewModel.logout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("brand_profile_logout")
        ) {
            Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
