package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.data.UserProfile
import com.example.data.UserSessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val parent: CreatorHubViewModel,
    private val application: Application
) {
    // Navigate and session flow state
    val currentScreen = MutableStateFlow("onboarding")
    val userSessionState = MutableStateFlow<UserSessionState>(UserSessionState.Check)
    val selectedRole = MutableStateFlow("creator") // "creator" or "brand"
    val authError = MutableStateFlow<String?>(null)
    val authLoading = MutableStateFlow(false)

    fun navigateTo(screen: String) {
        currentScreen.value = screen
    }

    fun clearAuthError() {
        authError.value = null
    }

    fun setOnboardingSeen() {
        val prefs = application.getSharedPreferences("creator_hub_profile", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("seen_onboarding", true).apply()
        navigateTo("welcome")
    }

    fun login(email: String, password: String, role: String) {
        if (email.isBlank() || password.isBlank()) {
            authError.value = "Email and Password cannot be empty"
            return
        }
        authLoading.value = true
        authError.value = null

        selectedRole.value = role

        if (parent.isLocalDemoMode) {
            parent.viewModelScope.launch {
                kotlinx.coroutines.delay(800)
                authLoading.value = false
                userSessionState.value = UserSessionState.Authenticated(
                    email = email.trim(),
                    uid = "local_demo_uid"
                )
                // Initialize custom profile sprint fields locally
                val profile = parent.profileViewModel.loadProfileLocally("local_demo_uid", email.trim()).copy(role = role)
                parent.profileViewModel.userProfile.value = profile
                parent.profileViewModel.saveProfileLocally(profile)
                parent.opportunityViewModel.syncLocalDataToStateFlows()
                if (role == "brand") {
                    navigateTo("campaign_management")
                } else {
                    navigateTo("opportunities")
                }
            }
            return
        }

        parent.firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                authLoading.value = false
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid ?: ""
                    parent.profileViewModel.fetchOrCreateUserProfile(uid, email.trim())
                } else {
                    authError.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }

    fun login(email: String, password: String) {
        login(email, password, "creator")
    }

    fun signupCreator(name: String, email: String, password: String, instagram: String, youtube: String, category: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            authError.value = "Name, Email, and Password cannot be empty"
            return
        }
        if (password.length < 6) {
            authError.value = "Password should be at least 6 characters"
            return
        }
        authLoading.value = true
        authError.value = null

        selectedRole.value = "creator"

        val profile = UserProfile(
            uid = "local_demo_uid",
            email = email.trim(),
            name = name.trim(),
            instagramFollowers = instagram,
            youtubeSubscribers = youtube,
            categories = category,
            role = "creator"
        )

        if (parent.isLocalDemoMode) {
            parent.viewModelScope.launch {
                kotlinx.coroutines.delay(800)
                authLoading.value = false
                userSessionState.value = UserSessionState.Authenticated(
                    email = email.trim(),
                    uid = "local_demo_uid"
                )
                parent.profileViewModel.userProfile.value = profile
                parent.profileViewModel.saveProfileLocally(profile)
                parent.opportunityViewModel.syncLocalDataToStateFlows()
                navigateTo("opportunities")
            }
            return
        }

        parent.firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                authLoading.value = false
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid ?: ""
                    val updatedProfile = profile.copy(uid = uid)
                    parent.profileViewModel.userProfile.value = updatedProfile
                    parent.profileViewModel.saveProfileLocally(updatedProfile)
                    parent.firestore.collection("users").document(uid).set(updatedProfile)
                    navigateTo("opportunities")
                } else {
                    authError.value = task.exception?.localizedMessage ?: "Signup failed"
                }
            }
    }

    fun signupBrand(companyName: String, email: String, password: String, website: String, industry: String) {
        if (email.isBlank() || password.isBlank() || companyName.isBlank()) {
            authError.value = "Company Name, Email, and Password cannot be empty"
            return
        }
        if (password.length < 6) {
            authError.value = "Password should be at least 6 characters"
            return
        }
        authLoading.value = true
        authError.value = null

        selectedRole.value = "brand"

        val profile = UserProfile(
            uid = "local_demo_uid",
            email = email.trim(),
            name = companyName.trim(),
            website = website.trim(),
            industry = industry,
            role = "brand"
        )

        if (parent.isLocalDemoMode) {
            parent.viewModelScope.launch {
                kotlinx.coroutines.delay(800)
                authLoading.value = false
                userSessionState.value = UserSessionState.Authenticated(
                    email = email.trim(),
                    uid = "local_demo_uid"
                )
                parent.profileViewModel.userProfile.value = profile
                parent.profileViewModel.saveProfileLocally(profile)
                parent.opportunityViewModel.syncLocalDataToStateFlows()
                navigateTo("campaign_management")
            }
            return
        }

        parent.firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                authLoading.value = false
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid ?: ""
                    val updatedProfile = profile.copy(uid = uid)
                    parent.profileViewModel.userProfile.value = updatedProfile
                    parent.profileViewModel.saveProfileLocally(updatedProfile)
                    parent.firestore.collection("users").document(uid).set(updatedProfile)
                    navigateTo("campaign_management")
                } else {
                    authError.value = task.exception?.localizedMessage ?: "Signup failed"
                }
            }
    }

    fun signup(email: String, password: String) {
        signupCreator(
            name = "Creator User",
            email = email,
            password = password,
            instagram = "10K",
            youtube = "5K",
            category = "General"
        )
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            authError.value = "Please enter your email to reset password"
            return
        }
        authLoading.value = true
        authError.value = null

        if (parent.isLocalDemoMode) {
            parent.viewModelScope.launch {
                kotlinx.coroutines.delay(600)
                authLoading.value = false
                authError.value = "Password reset email sent successfully! (Local Demo)"
            }
            return
        }

        parent.firebaseAuth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                authLoading.value = false
                if (task.isSuccessful) {
                    authError.value = "Password reset email sent successfully!"
                } else {
                    authError.value = task.exception?.localizedMessage ?: "Reset failed"
                }
            }
    }

    fun logout() {
        if (parent.isLocalDemoMode) {
            userSessionState.value = UserSessionState.Unauthenticated
            parent.profileViewModel.userProfile.value = null
            navigateTo("login")
            return
        }
        parent.firebaseAuth.signOut()
        navigateTo("login")
    }
}
