package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(viewModel: CreatorHubViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CreatorHubLogo(iconSize = 48.dp, fontSize = 24.sp)
        
        Spacer(modifier = Modifier.height(28.dp))
        
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Sign in to access secure escrow portals & campaigns",
            style = MaterialTheme.typography.bodyMedium,
            color = AppGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!authError.isNullOrEmpty()) {
            Surface(
                color = CoralRed.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, CoralRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = authError!!,
                    color = CoralRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Escrow Email", color = ElectricCyan) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = ElectricCyan) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("login_email")
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Security Password", color = ElectricCyan) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = ElectricCyan) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = AppGray
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("login_password")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { viewModel.navigateTo("forgot_password") }
            ) {
                Text(
                    text = "Forgot Password?",
                    color = AppGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("login_submit_button")
        ) {
            Text(
                text = "Sign In securely",
                color = CosmicNavy,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an escrow account?", color = TextSecondary)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = { viewModel.navigateTo("welcome") }
            ) {
                Text(
                    text = "Join Hub",
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SignupScreen(viewModel: CreatorHubViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CreatorHubLogo(iconSize = 48.dp, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Create Escrow Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Secure your custom platform agreements and campaigns.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!authError.isNullOrEmpty()) {
            Surface(
                color = CoralRed.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, CoralRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = authError!!,
                    color = CoralRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Work Email", color = ElectricCyan) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = ElectricCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_email")
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Choose Password", color = ElectricCyan) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = ElectricCyan) },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_password")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.signup(email, password) },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("signup_submit_button")
        ) {
            Text(
                text = "Register with Escrow",
                color = CosmicNavy,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Registered already?", color = TextSecondary)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = { viewModel.navigateTo("login") }
            ) {
                Text("Sign In", color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(viewModel: CreatorHubViewModel) {
    var email by remember { mutableStateOf("") }
    var sentMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CreatorHubLogo(iconSize = 48.dp, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Reset Pass Code",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Enter your work email below. We'll send security codes to reset password.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (sentMessage != null) {
            Surface(
                color = LightSageGlow.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, LightSageGlow.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = sentMessage!!,
                    color = LightSageGlow,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Work Email", color = ElectricCyan) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = ElectricCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("forgot_email")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    sentMessage = "Security reset emails dispatched to $email safely!"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("forgot_submit_button")
        ) {
            Text(
                text = "Dispatch Security Link",
                color = CosmicNavy,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.navigateTo("login") }
        ) {
            Text("Back to Sign In", color = ElectricCyan, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SignupCreatorScreen(viewModel: CreatorHubViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var followersCount by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CreatorHubLogo(iconSize = 44.dp, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Join as Creator Partner",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "India's professional escrow-backed marketplace",
            style = MaterialTheme.typography.bodySmall,
            color = AppGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!authError.isNullOrEmpty()) {
            Surface(
                color = CoralRed.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, CoralRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    text = authError!!,
                    color = CoralRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Escrow Work Email", color = ElectricCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_creator_email")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Security Access Code", color = ElectricCyan) },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_creator_password")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = specialty,
            onValueChange = { specialty = it },
            label = { Text("Primary Speciality (e.g. Photography, Tech)", color = ElectricCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_creator_specialty")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = followersCount,
            onValueChange = { followersCount = it },
            label = { Text("Approx aggregate followers (e.g. 50K)", color = ElectricCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_creator_followers")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                val creatorName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                viewModel.signupCreator(
                    name = creatorName,
                    email = email,
                    password = password,
                    instagram = "@$creatorName",
                    youtube = followersCount,
                    category = specialty
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_creator_signup")
        ) {
            Text(
                text = "Apply Creator Registry",
                color = CosmicNavy,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.navigateTo("welcome") }
        ) {
            Text("Back to Role Check", color = ElectricCyan, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SignupBrandScreen(viewModel: CreatorHubViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CreatorHubLogo(iconSize = 44.dp, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Join as Brand Partner",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Initiate verified campaigns with Indian Creators securely",
            style = MaterialTheme.typography.bodySmall,
            color = AppGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!authError.isNullOrEmpty()) {
            Surface(
                color = CoralRed.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, CoralRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    text = authError!!,
                    color = CoralRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Corporate Partner Email", color = ElectricCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_brand_email")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Corporate Access Code", color = ElectricCyan) },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_brand_password")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = brandName,
            onValueChange = { brandName = it },
            label = { Text("Registered Company / Brand Name", color = ElectricCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_brand_name")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Headquarters Location", color = ElectricCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = NeonPurple
            ),
            modifier = Modifier.fillMaxWidth().testTag("signup_brand_location")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                viewModel.signupBrand(
                    companyName = brandName,
                    email = email,
                    password = password,
                    website = "https://${brandName.lowercase().replace(" ", "")}.com",
                    industry = location
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_brand_signup")
        ) {
            Text(
                text = "Secure Corporate Account",
                color = CosmicNavy,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.navigateTo("welcome") }
        ) {
            Text("Back to Role Check", color = ElectricCyan, fontWeight = FontWeight.Bold)
        }
    }
}
