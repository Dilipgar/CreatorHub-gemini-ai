package com.example.data

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val name: String = "Ankit Photographer",
    val handle: String = "@ankitclicks",
    val location: String = "Mumbai, IN",
    val bio: String = "I generate premium lifestyle content and social media vlogs focusing on architectural landmarks, drone landscape cinematography, travel storytelling, and luxury retreats. Dedicated to delivering high-CTR assets with zero-dispute contract compliance.",
    val tags: List<String> = listOf("Travel Cinema", "Elite Partner"),
    val instagramFollowers: String = "125K",
    val youtubeSubscribers: String = "92K",
    val twitterFollowers: String = "14K",
    val creatorScore: Int = 94,
    val trustRating: Int = 98,
    val totalSecuredEarnings: String = "₹4,85,000",
    val heldInEscrow: String = "₹75,000",
    val activeContractsCount: Int = 8
)
