package com.example.ui

import android.app.Application
import android.content.Context
import com.example.data.NotificationItem
import com.example.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow

class ProfileViewModel(
    private val parent: CreatorHubViewModel,
    private val application: Application
) {
    val userProfile = MutableStateFlow<UserProfile?>(null)
    val notifications = MutableStateFlow<List<NotificationItem>>(emptyList())

    fun fetchOrCreateUserProfile(uid: String, email: String) {
        parent.firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    try {
                        val nameVal = document.getString("name") ?: "Ankit Photographer"
                        val handleVal = document.getString("handle") ?: ("@" + email.substringBefore("@"))
                        val locationVal = document.getString("location") ?: "Mumbai, IN"
                        val bioVal = document.getString("bio") ?: "I generate premium lifestyle content and social media vlogs..."
                        val tagsVal = document.get("tags") as? List<String> ?: listOf("Travel Cinema", "Elite Partner")
                        val instagramFollowers = document.getString("instagramFollowers") ?: "125K"
                        val youtubeSubscribers = document.getString("youtubeSubscribers") ?: "92K"
                        val twitterFollowers = document.getString("twitterFollowers") ?: "14K"
                        val creatorScore = document.getLong("creatorScore")?.toInt() ?: 94
                        val trustRating = document.getLong("trustRating")?.toInt() ?: 98
                        val totalSecuredEarnings = document.getString("totalSecuredEarnings") ?: "₹4,85,000"
                        val heldInEscrow = document.getString("heldInEscrow") ?: "₹75,000"
                        val activeContractsCount = document.getLong("activeContractsCount")?.toInt() ?: 8

                        val roleVal = document.getString("role") ?: "creator"
                        val industryVal = document.getString("industry") ?: ""

                        val profile = UserProfile(
                            uid = uid,
                            email = email,
                            name = nameVal,
                            handle = handleVal,
                            location = locationVal,
                            bio = bioVal,
                            tags = tagsVal,
                            instagramFollowers = instagramFollowers,
                            youtubeSubscribers = youtubeSubscribers,
                            twitterFollowers = twitterFollowers,
                            creatorScore = creatorScore,
                            trustRating = trustRating,
                            totalSecuredEarnings = totalSecuredEarnings,
                            heldInEscrow = heldInEscrow,
                            activeContractsCount = activeContractsCount,
                            role = roleVal,
                            industry = industryVal
                        )
                        userProfile.value = profile
                        parent.authViewModel.selectedRole.value = roleVal
                        
                        // Role based redirect on auth success
                        if (roleVal == "brand") {
                            parent.authViewModel.navigateTo("campaign_management")
                        } else {
                            parent.authViewModel.navigateTo("opportunities")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        userProfile.value = UserProfile(uid = uid, email = email)
                    }
                } else {
                    val defaultProfile = UserProfile(
                        uid = uid,
                        email = email,
                        name = "Ankit Photographer",
                        handle = "@" + email.substringBefore("@")
                    )
                    parent.firestore.collection("users").document(uid).set(defaultProfile)
                        .addOnSuccessListener {
                            userProfile.value = defaultProfile
                        }
                }
            }
            .addOnFailureListener {
                userProfile.value = UserProfile(uid = uid, email = email)
            }
    }

    fun saveProfileLocally(profile: UserProfile) {
        val prefs = application.getSharedPreferences("creator_hub_profile", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("uid", profile.uid)
            putString("email", profile.email)
            putString("name", profile.name)
            putString("handle", profile.handle)
            putString("location", profile.location)
            putString("bio", profile.bio)
            putString("instagramFollowers", profile.instagramFollowers)
            putString("youtubeSubscribers", profile.youtubeSubscribers)
            putString("twitterFollowers", profile.twitterFollowers)
            putInt("creatorScore", profile.creatorScore)
            putInt("trustRating", profile.trustRating)
            putString("totalSecuredEarnings", profile.totalSecuredEarnings)
            putString("heldInEscrow", profile.heldInEscrow)
            putInt("activeContractsCount", profile.activeContractsCount)
            
            // Custom profile fields
            putString("profilePhoto", profile.profilePhoto)
            putString("instagramUrl", profile.instagramUrl)
            putString("youtubeUrl", profile.youtubeUrl)
            putString("website", profile.website)
            putString("categories", profile.categories)
            putString("followers", profile.followers)
            putString("portfolioLinks", profile.portfolioLinks)
            putString("role", profile.role)
            putString("industry", profile.industry)
            apply()
        }
    }

    fun loadProfileLocally(uid: String, email: String): UserProfile {
        val prefs = application.getSharedPreferences("creator_hub_profile", Context.MODE_PRIVATE)
        if (!prefs.contains("name")) {
            return UserProfile(
                uid = uid,
                email = email,
                name = "Ankit Photographer",
                handle = "@ankitclicks",
                location = "Mumbai, IN",
                role = prefs.getString("role", "creator") ?: "creator",
                industry = prefs.getString("industry", "") ?: ""
            )
        }
        return UserProfile(
            uid = prefs.getString("uid", uid) ?: uid,
            email = prefs.getString("email", email) ?: email,
            name = prefs.getString("name", "Ankit Photographer") ?: "Ankit Photographer",
            handle = prefs.getString("handle", "@ankitclicks") ?: "@ankitclicks",
            location = prefs.getString("location", "Mumbai, IN") ?: "Mumbai, IN",
            bio = prefs.getString("bio", "") ?: "",
            instagramFollowers = prefs.getString("instagramFollowers", "125K") ?: "125K",
            youtubeSubscribers = prefs.getString("youtubeSubscribers", "92K") ?: "92K",
            twitterFollowers = prefs.getString("twitterFollowers", "14K") ?: "14K",
            creatorScore = prefs.getInt("creatorScore", 94),
            trustRating = prefs.getInt("trustRating", 98),
            totalSecuredEarnings = prefs.getString("totalSecuredEarnings", "₹4,85,000") ?: "₹4,85,000",
            heldInEscrow = prefs.getString("heldInEscrow", "₹75,000") ?: "₹75,000",
            activeContractsCount = prefs.getInt("activeContractsCount", 8),
            
            profilePhoto = prefs.getString("profilePhoto", "") ?: "",
            instagramUrl = prefs.getString("instagramUrl", "https://instagram.com/ankitclicks") ?: "https://instagram.com/ankitclicks",
            youtubeUrl = prefs.getString("youtubeUrl", "https://youtube.com/c/ankitvlogs") ?: "https://youtube.com/c/ankitvlogs",
            website = prefs.getString("website", "https://ankitclicks.com") ?: "https://ankitclicks.com",
            categories = prefs.getString("categories", "Photography, Travel, Videography") ?: "Photography, Travel, Videography",
            followers = prefs.getString("followers", "231K") ?: "231K",
            portfolioLinks = prefs.getString("portfolioLinks", "https://behance.net/ankitclicks, https://unsplash.com/@ankitclicks") ?: "https://behance.net/ankitclicks, https://unsplash.com/@ankitclicks",
            role = prefs.getString("role", "creator") ?: "creator",
            industry = prefs.getString("industry", "") ?: ""
        )
    }

    fun updateUserProfile(profile: UserProfile) {
        val uid = profile.uid.takeIf { it.isNotEmpty() } ?: return
        userProfile.value = profile
        saveProfileLocally(profile)
        if (!parent.isLocalDemoMode) {
            try {
                parent.firestore.collection("users").document(uid).set(profile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Post a notification about profile update
        val newNotification = NotificationItem(
            id = (notifications.value.maxOfOrNull { it.id } ?: 0) + 1,
            title = "Profile Synced Successful!",
            body = "Your professional portal was synced and saved locally.",
            timestamp = "Just now",
            type = "profile"
        )
        notifications.value = listOf(newNotification) + notifications.value
    }
}
