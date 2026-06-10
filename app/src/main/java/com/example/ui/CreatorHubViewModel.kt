package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

sealed class UserSessionState {
    object Check : UserSessionState()
    object Unauthenticated : UserSessionState()
    data class Authenticated(val email: String, val uid: String) : UserSessionState()
}

class CreatorHubViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CreatorHubRepository = CreatorHubRepository(AppDatabase.getDatabase(application))
    private var firebaseAuth: FirebaseAuth
    private var firestore: FirebaseFirestore
    private var isLocalDemoMode = true

    // Active screen navigation tracking inside local Composable navigation
    var currentScreen = MutableStateFlow("onboarding")
        private set

    var selectedOpportunityId = MutableStateFlow<Int?>(null)
        private set

    var activeChatSessionId = MutableStateFlow<Int?>(null)
        private set

    var selectedAffiliateOfferId = MutableStateFlow<Int?>(null)
        private set

    val selectedAffiliateOffer: StateFlow<AffiliateOfferEntity?> = selectedAffiliateOfferId
        .flatMapLatest { id ->
            if (id != null) repository.getAffiliateOffer(id) else flowOf(null)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allAffiliateOffers: StateFlow<List<AffiliateOfferEntity>> = repository.affiliateOffers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val affiliateEarnings: StateFlow<List<AffiliateEarningEntity>> = repository.affiliateEarnings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var affiliateSearchQuery = MutableStateFlow("")
        private set

    var affiliateCategoryFilter = MutableStateFlow("All") // "All", "SaaS & Hosting", "Design & Creative", "E-commerce & Retail", "Education & Self-Care"
        private set

    val filteredAffiliateOffers: StateFlow<List<AffiliateOfferEntity>> = combine(
        allAffiliateOffers,
        affiliateSearchQuery,
        affiliateCategoryFilter
    ) { offers, query, cat ->
        offers.filter { offer ->
            val matchesQuery = offer.title.contains(query, ignoreCase = true) ||
                    offer.brandName.contains(query, ignoreCase = true)
            val matchesCategory = if (cat == "All") true else offer.category == cat
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSessionState = MutableStateFlow<UserSessionState>(UserSessionState.Check)
    val selectedRole = MutableStateFlow<String>("creator") // "creator" or "brand"

    var authError = MutableStateFlow<String?>(null)
        private set

    var authLoading = MutableStateFlow(false)
        private set

    // Firestore-backed StateFlows
    val firestoreOpportunities = MutableStateFlow<List<OpportunityEntity>>(emptyList())
    val firestoreDeals = MutableStateFlow<List<DealEntity>>(emptyList())
    val userProfile = MutableStateFlow<UserProfile?>(null)
    val notifications = MutableStateFlow<List<NotificationItem>>(emptyList())

    // Search and tab filters
    var searchQuery = MutableStateFlow("")
        private set
    var activeTabFilter = MutableStateFlow("All") // "All", "Brand Deals", "Affiliate", "Collab", "Contest"
        private set

    init {
        try {
            if (FirebaseApp.getApps(application).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey("AIzaSyFakeKeyForLocalCompileOnly")
                    .setApplicationId("1:fake-app-id-for-compile-only")
                    .setProjectId("fake-project-id")
                    .build()
                FirebaseApp.initializeApp(application, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        isLocalDemoMode = try {
            val app = FirebaseApp.getInstance()
            app.options.apiKey == "AIzaSyFakeKeyForLocalCompileOnly"
        } catch (e: Exception) {
            true
        }

        val database = AppDatabase.getDatabase(application)

        if (isLocalDemoMode) {
            syncLocalDataToStateFlows()
            userProfile.value = loadProfileLocally("local_demo_uid", "dilip.goswami96@gmail.com")
            notifications.value = listOf(
                NotificationItem(1, "Welcome to CreatorHub!", "Complete your profile edit sprint to start landing verified escrow deals.", "10 mins ago", "system", false),
                NotificationItem(2, "Escrow Secured", "boAt Lifestyle reviewed your portfolio and locked ₹50,000 in safe escrow.", "1 hour ago", "deal", false),
                NotificationItem(3, "Campaign Trending", "GoPro Action Shorts is seeing high application rates in Travel category.", "1 day ago", "campaign", false)
            )
            userSessionState.value = UserSessionState.Unauthenticated
            val prefs = application.getSharedPreferences("creator_hub_profile", Context.MODE_PRIVATE)
            val hasSeenOnboarding = prefs.getBoolean("seen_onboarding", false)
            if (!hasSeenOnboarding) {
                currentScreen.value = "onboarding"
            } else {
                if (currentScreen.value != "signup" && currentScreen.value != "forgot_password") {
                    currentScreen.value = "login"
                }
            }
        } else {
            // Sync opportunities immediately
            syncAndListenOpportunities()

            // Listen to Firebase Auth state changes
            firebaseAuth.addAuthStateListener { auth ->
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    userSessionState.value = UserSessionState.Authenticated(
                        email = firebaseUser.email ?: "",
                        uid = firebaseUser.uid
                    )
                    // Fetch or automatically initialize user profile in Firestore
                    fetchOrCreateUserProfile(firebaseUser.uid, firebaseUser.email ?: "")
                    // Listen to deals associated with the user
                    syncAndListenDeals()
                    
                    if (currentScreen.value == "login" || currentScreen.value == "signup" || currentScreen.value == "forgot_password") {
                        currentScreen.value = "opportunities"
                    }
                } else {
                    userSessionState.value = UserSessionState.Unauthenticated
                    userProfile.value = null
                    firestoreDeals.value = emptyList()
                    if (currentScreen.value != "signup" && currentScreen.value != "forgot_password") {
                        currentScreen.value = "login"
                    }
                }
            }
        }
    }

    private fun syncLocalDataToStateFlows() {
        viewModelScope.launch {
            repository.opportunities.collect { list ->
                firestoreOpportunities.value = list
            }
        }
        viewModelScope.launch {
            repository.activeDeals.collect { list ->
                firestoreDeals.value = list
            }
        }
    }

    // Exposed Flows (Mapped directly to repository for 100% reliable local stability & offline persistence!)
    val allOpportunities: StateFlow<List<OpportunityEntity>> = repository.opportunities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatSessions: StateFlow<List<ChatSessionEntity>> = repository.chatSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDeals: StateFlow<List<DealEntity>> = repository.activeDeals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Opportunities based on Search Query & Tab
    val filteredOpportunities: StateFlow<List<OpportunityEntity>> = combine(
        allOpportunities,
        searchQuery,
        activeTabFilter
    ) { ops, query, tab ->
        ops.filter { op ->
            val matchesQuery = op.title.contains(query, ignoreCase = true) ||
                    op.brandName.contains(query, ignoreCase = true) ||
                    op.platform.contains(query, ignoreCase = true)
            
            val matchesTab = when (tab) {
                "All" -> true
                "Brand Deals" -> op.type == "Brand Deal"
                "Affiliate" -> op.type == "Affiliate"
                "Collab" -> op.type == "Collab"
                "Contest" -> op.type == "Contest"
                else -> true
            }
            matchesQuery && matchesTab
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stream for selected opportunity derived through local repository opportunities
    val selectedOpportunity: StateFlow<OpportunityEntity?> = selectedOpportunityId
        .combine(allOpportunities) { id, ops ->
            ops.find { it.id == id }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Stream for selected opportunity's deal state derived through local repository deals (applied or in progress)
    val selectedOpportunityDeal: StateFlow<DealEntity?> = selectedOpportunityId
        .combine(activeDeals) { id, deals ->
            deals.find { it.opportunityId == id }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Stream of messages for selected chat (using local cache Room repository)
    val chatMessages: StateFlow<List<MessageEntity>> = activeChatSessionId
        .flatMapLatest { id ->
            if (id != null) repository.getMessages(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun navigateTo(screen: String) {
        currentScreen.value = screen
    }

    fun selectOpportunity(id: Int) {
        selectedOpportunityId.value = id
        navigateTo("details")
    }

    fun selectAffiliateOffer(id: Int) {
        selectedAffiliateOfferId.value = id
        navigateTo("affiliate_details")
    }

    fun setAffiliateSearchQuery(query: String) {
        affiliateSearchQuery.value = query
    }

    fun setAffiliateCategoryFilter(category: String) {
        affiliateCategoryFilter.value = category
    }

    fun toggleSaveAffiliateOffer(id: Int) {
        viewModelScope.launch {
            repository.toggleSaveAffiliateOffer(id)
        }
    }

    fun applyToAffiliateOffer(id: Int, brandName: String) {
        viewModelScope.launch {
            val handleClean = userProfile.value?.handle?.removePrefix("@") ?: "ankitclicks"
            val brandClean = brandName.lowercase().replace(" ", "")
            val trackingLink = "https://creatorhub.link/ref/$handleClean/$brandClean"
            repository.applyToAffiliateOffer(id, trackingLink)

            // Start an interactive chat introduction with the affiliate support manager
            val welcomeMsg = "Congratulations! Your affiliate partner request for $brandName has been auto-approved. Your verified status: ELITE PARTNER. Use your custom tracking link inside your bio and posts to maximize commission earnings immediately."
            repository.startChatSession("$brandName Affiliate", "YouTube", welcomeMsg)
        }
    }

    fun simulateClickOnAffiliate(offerId: Int) {
        viewModelScope.launch {
            val earningsList = affiliateEarnings.value
            val activeEarning = earningsList.find { it.offerId == offerId }
            if (activeEarning != null) {
                val nextClicks = activeEarning.clicksCount + 1
                val isConversion = (1..100).random() <= 8 // 8% conversion rate
                
                var nextConversions = activeEarning.conversionsCount
                var nextSales = activeEarning.totalSales
                var nextEarnings = activeEarning.earningsAmount
                
                if (isConversion) {
                    nextConversions += 1
                    
                    // Sales amount generated ranges between 1000 and 5000
                    val saleVal = (1000..5000).random().toDouble()
                    nextSales += saleVal
                    
                    // Earned commission based on rate or flat
                    val commissionRateText = allAffiliateOffers.value.find { it.id == offerId }?.commissionRate ?: "10%"
                    val computedEarning = if (commissionRateText.contains("₹")) {
                        commissionRateText.filter { it.isDigit() }.toDoubleOrNull() ?: 500.0
                    } else {
                        val percentage = commissionRateText.filter { it.isDigit() }.toDoubleOrNull() ?: 10.0
                        (saleVal * percentage) / 100.0
                    }
                    nextEarnings += computedEarning
                }

                repository.insertAffiliateEarning(
                    activeEarning.copy(
                        clicksCount = nextClicks,
                        conversionsCount = nextConversions,
                        totalSales = nextSales,
                        earningsAmount = nextEarnings,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun selectChatSession(id: Int) {
        activeChatSessionId.value = id
        navigateTo("chat_detail")
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setActiveTab(tab: String) {
        activeTabFilter.value = tab
    }

    fun toggleSaveOpportunity(op: OpportunityEntity) {
        viewModelScope.launch {
            val nextSaved = !op.isSaved
            repository.updateOpportunity(op.copy(isSaved = nextSaved))
            if (!isLocalDemoMode) {
                // Save to Firestore!
                firestore.collection("opportunities").document(op.id.toString())
                    .update("isSaved", nextSaved)
                    .addOnFailureListener {
                        // fall back or ignore
                    }
            }
        }
    }

    // Apply to opportunity (triggers "Applied" status and registers in both local Room and remote Firestore)
    fun applyForOpportunity(opId: Int, title: String, brandName: String, proposedBudget: String) {
        viewModelScope.launch {
            repository.applyToOpportunity(opId, title, brandName, proposedBudget)
            
            if (!isLocalDemoMode) {
                // Save to Firestore!
                val user = firebaseAuth.currentUser
                val uid = user?.uid ?: "default_uid"
                val newId = (firestoreDeals.value.maxOfOrNull { it.id } ?: 0) + 1
                val docData = mapOf(
                    "id" to newId,
                    "opportunityId" to opId,
                    "title" to title,
                    "brandName" to brandName,
                    "dealAmount" to proposedBudget,
                    "status" to "Applied",
                    "timestamp" to System.currentTimeMillis(),
                    "uid" to uid
                )
                firestore.collection("deals").document(newId.toString()).set(docData)
            }

            // Auto-create a local message in prototype chat
            val welcomeMsg = "Hi, I have just submitted my pitch and application for the '$title' campaign! I am incredibly excited to collaborate with you."
            repository.startChatSession(brandName, "YouTube", welcomeMsg)
        }
    }

    // Advances the deal's standard flow inside Firestore and Room
    fun advanceDealStatus(deal: DealEntity) {
        viewModelScope.launch {
            val nextStatus = when (deal.status) {
                "Applied" -> "Accepted" // Brand accepts, adds funds
                "Accepted" -> "Work Submitted" // Creator does work
                "Work Submitted" -> "Under Review" // Brand reviews
                "Under Review" -> "Completed" // Brand approves
                "Completed" -> "Payment Released" // Escrow releases
                else -> "Applied" // Reset for demo lifecycle loop
            }
            repository.updateDealStatus(deal.id, nextStatus)

            if (!isLocalDemoMode) {
                try {
                    // Update in Firestore!
                    firestore.collection("deals").document(deal.id.toString())
                        .update("status", nextStatus)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Post a custom deal escrow milestone notification
            val newNotification = NotificationItem(
                id = (notifications.value.maxOfOrNull { it.id } ?: 0) + 1,
                title = "Milestone: $nextStatus",
                body = "Your escrow contract with ${deal.brandName} is now in status '$nextStatus'.",
                timestamp = "Just now",
                type = "deal"
            )
            notifications.value = listOf(newNotification) + notifications.value
        }
    }

    fun sendMessageToActiveChat(text: String) {
        val sId = activeChatSessionId.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(sId, text, "creator")
            
            // Auto-respond for a fun, interactive prototype experience!
            simulateBrandReply(sId, text)
        }
    }

    private fun simulateBrandReply(sessionId: Int, creatorMsg: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500) // realistic wait
            val sessionName = chatSessions.value.find { it.id == sessionId }?.partnerName ?: "Brand Partner"
            val responseText = when {
                creatorMsg.contains("hello", ignoreCase = true) || creatorMsg.contains("hi", ignoreCase = true) -> {
                    "Hello Ankit! Thanks for writing to us. We're reviewing your concepts."
                }
                creatorMsg.contains("pitch", ignoreCase = true) || creatorMsg.contains("apply", ignoreCase = true) -> {
                    "Thanks for the application pitch! Your stats look amazing, and we love your photography portfolio style."
                }
                creatorMsg.contains("budget", ignoreCase = true) || creatorMsg.contains("rate", ignoreCase = true) -> {
                    "Our budget in escrow can go up to the maximum limit specified in the deal details for premium content quality."
                }
                creatorMsg.contains("submit", ignoreCase = true) || creatorMsg.contains("completed", ignoreCase = true) -> {
                    "Excellent work! We will review the draft and release the milestone funds shortly."
                }
                else -> {
                    "Got it! Let's get things moving. Let us sync with our campaign heads and update back here soon."
                }
            }
            repository.sendMessage(sessionId, responseText, "brand")
        }
    }

    // Create custom opportunity (From + / Create tab in UI)
    fun createCustomOpportunity(
        title: String,
        brandName: String,
        budgetRange: String,
        type: String,
        platform: String,
        requirements: String,
        aboutCampaign: String
    ) {
        viewModelScope.launch {
            val newId = (allOpportunities.value.maxOfOrNull { it.id } ?: 0) + 1
            val op = OpportunityEntity(
                id = newId,
                title = title,
                brandName = brandName,
                budgetRange = budgetRange,
                type = type,
                platform = platform,
                location = "India",
                durationText = "Ends in 14 days",
                requirements = requirements,
                aboutCampaign = aboutCampaign
            )
            repository.insertOpportunity(op)

            if (!isLocalDemoMode) {
                try {
                    // Save to Firestore!
                    val docData = mapOf(
                        "id" to op.id,
                        "title" to op.title,
                        "brandName" to op.brandName,
                        "budgetRange" to op.budgetRange,
                        "type" to op.type,
                        "platform" to op.platform,
                        "location" to op.location,
                        "durationText" to op.durationText,
                        "requirements" to op.requirements,
                        "aboutCampaign" to op.aboutCampaign,
                        "isSaved" to op.isSaved,
                        "commissionRate" to op.commissionRate,
                        "difficultyLevel" to op.difficultyLevel,
                        "category" to op.category
                    )
                    firestore.collection("opportunities").document(op.id.toString()).set(docData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Post a system notification about campaign publication
            val newNotification = NotificationItem(
                id = (notifications.value.maxOfOrNull { it.id } ?: 0) + 1,
                title = "Campaign Published Successful!",
                body = "Your custom campaign '$title' for $brandName was successfully published.",
                timestamp = "Just now",
                type = "campaign"
            )
            notifications.value = listOf(newNotification) + notifications.value

            navigateTo("opportunities")
        }
    }

    // --- FIRESTORE USER PROFILE API ---
    fun fetchOrCreateUserProfile(uid: String, email: String) {
        firestore.collection("users").document(uid).get()
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
                        selectedRole.value = roleVal
                        
                        // Role based redirect on auth success
                        if (roleVal == "brand") {
                            navigateTo("campaign_management")
                        } else {
                            navigateTo("opportunities")
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
                    firestore.collection("users").document(uid).set(defaultProfile)
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
        val prefs = getApplication<Application>().getSharedPreferences("creator_hub_profile", Context.MODE_PRIVATE)
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
            
            // Custom profile sprint fields
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
        val prefs = getApplication<Application>().getSharedPreferences("creator_hub_profile", Context.MODE_PRIVATE)
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
        if (!isLocalDemoMode) {
            try {
                firestore.collection("users").document(uid).set(profile)
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

    // --- FIRESTORE SYNC & SEED METHODS ---
    fun syncAndListenOpportunities() {
        firestore.collection("opportunities").addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot == null || snapshot.isEmpty) {
                seedFirestoreOpportunities()
            } else {
                val list = mutableListOf<OpportunityEntity>()
                for (doc in snapshot.documents) {
                    try {
                        val title = doc.getString("title") ?: ""
                        val brandName = doc.getString("brandName") ?: ""
                        val budgetRange = doc.getString("budgetRange") ?: ""
                        val type = doc.getString("type") ?: ""
                        val platform = doc.getString("platform") ?: ""
                        val location = doc.getString("location") ?: ""
                        val durationText = doc.getString("durationText") ?: ""
                        val requirements = doc.getString("requirements") ?: ""
                        val aboutCampaign = doc.getString("aboutCampaign") ?: ""
                        val isSaved = doc.getBoolean("isSaved") ?: false
                        val commissionRate = doc.getString("commissionRate") ?: ""
                        val difficultyLevel = doc.getString("difficultyLevel") ?: ""
                        val category = doc.getString("category") ?: ""
                        val idVal = doc.getLong("id")?.toInt() ?: doc.id.hashCode()

                        list.add(
                            OpportunityEntity(
                                id = idVal,
                                title = title,
                                brandName = brandName,
                                budgetRange = budgetRange,
                                type = type,
                                platform = platform,
                                location = location,
                                durationText = durationText,
                                requirements = requirements,
                                aboutCampaign = aboutCampaign,
                                isSaved = isSaved,
                                commissionRate = commissionRate,
                                difficultyLevel = difficultyLevel,
                                category = category
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                firestoreOpportunities.value = list
            }
        }
    }

    private fun seedFirestoreOpportunities() {
        val initialOps = listOf(
            OpportunityEntity(
                id = 1,
                title = "Tech Review for YouTube",
                brandName = "boAt Lifestyle",
                budgetRange = "₹25,000 - ₹75,000",
                type = "Brand Deal",
                platform = "YouTube",
                location = "India",
                durationText = "Ends in 5 days",
                requirements = "• Minimum 10k YouTube Subscribers\n• Good camera & audio quality\n• Honest review\n• Delivery in 7 days",
                aboutCampaign = "We are looking for tech creators who can review our new flagship ANC earbuds. Share your pure experience and direct audience to the launch link.",
                isSaved = false,
                difficultyLevel = "Intermediate",
                category = "Consumer Tech"
            ),
            OpportunityEntity(
                id = 2,
                title = "Promote Canva Pro",
                brandName = "Canva India",
                budgetRange = "Commission Base",
                type = "Affiliate",
                platform = "Instagram",
                location = "Remote",
                durationText = "Ends in 28 days",
                requirements = "• Passion for design/social media\n• Minimum 5k active followers\n• Weekly stories highlighting Canva tools\n• Generate unique signup link",
                aboutCampaign = "Spread the love for design. Create engaging reels showing modern layout shortcuts using Canva Pro. Higher conversions mean higher payouts!",
                isSaved = true,
                commissionRate = "Commission up to 40%",
                difficultyLevel = "Beginner Friendly",
                category = "Design SaaS"
            ),
            OpportunityEntity(
                id = 3,
                title = "Hostinger Creator Club",
                brandName = "Hostinger India",
                budgetRange = "Commission + Fixed",
                type = "Affiliate",
                platform = "YouTube",
                location = "Remote",
                durationText = "Ends in 45 days",
                requirements = "• Tech / Coding / Business content\n• Deliver dedicated web setup tutorials\n• Mention special coupon code\n• Put referral link in description",
                aboutCampaign = "Empower users to build websites. High conversion commission of up to 60% on hosting sales, plus a fixed cash bonus for generating over 50 sales.",
                isSaved = false,
                commissionRate = "Commission up to 60%",
                difficultyLevel = "Intermediate",
                category = "Hosting & Cloud"
            ),
            OpportunityEntity(
                id = 4,
                title = "Semrush SEO Expert Partners",
                brandName = "Semrush Tech",
                budgetRange = "Recurring Commission",
                type = "Affiliate",
                platform = "YouTube",
                location = "Global",
                durationText = "Ends in 60 days",
                requirements = "• Digital Marketing / SEO expert niche\n• Minimum 15k active email list or subs\n• In-depth review or webinar integration\n• High organic quality traffic",
                aboutCampaign = "Promote the elite tool for digital marketers. Earn recurring commissions for every custom trial user register who transfers into a premium active subscription.",
                isSaved = false,
                commissionRate = "Recurring up to 30%",
                difficultyLevel = "Expert Elite",
                category = "Enterprise SaaS"
            ),
            OpportunityEntity(
                id = 5,
                title = "Amazon Associates Premium",
                brandName = "Amazon India",
                budgetRange = "Sales Commission",
                type = "Affiliate",
                platform = "Instagram",
                location = "India",
                durationText = "No End Date",
                requirements = "• Home Decor / Lifestyle / Gadgets\n• Set up a curated amazon store link\n• Active stories linking trending products\n• Regular affiliate link mentions",
                aboutCampaign = "Promote anything sold on Amazon. Earn up to 12% affiliate commission from qualifying sales made using your personal storefront tracker links.",
                isSaved = false,
                commissionRate = "Commission up to 12%",
                difficultyLevel = "Beginner Friendly",
                category = "E-commerce Retail"
            ),
            OpportunityEntity(
                id = 6,
                title = "Travel Reel Creators",
                brandName = "Rajasthan Tourism",
                budgetRange = "Paid Collaboration",
                type = "Collab",
                platform = "Instagram",
                location = "Rajasthan, India",
                durationText = "Ends in 12 days",
                requirements = "• High-quality cinematography skills\n• Minimum 50k followers/travel niche\n• 3 Reels + 1 Carousel post\n• Must travel to specified locations",
                aboutCampaign = "Document the hidden historical jewels of desert forts. We provide complete accommodation, travel allowances, and exclusive access. Creator retains portfolio rights.",
                isSaved = false,
                difficultyLevel = "Intermediate",
                category = "Travel & Tourism"
            ),
            OpportunityEntity(
                id = 7,
                title = "GoPro Action Shorts",
                brandName = "GoPro India",
                budgetRange = "Product + ₹40,000",
                type = "Brand Deal",
                platform = "Instagram",
                location = "India",
                durationText = "Ends in 9 days",
                requirements = "• Action sports / adventure niche\n• Deliver 2 high energy cinematic reels\n• Raw video feed captured strictly on GoPro\n• Showcase stability and color grading",
                aboutCampaign = "Put our latest flagship action camera to the ultimate test. Show us your most creative adrenaline shots in urban or wilderness trails.",
                isSaved = false,
                difficultyLevel = "Expert Elite",
                category = "Adventure Tech"
            ),
            OpportunityEntity(
                id = 8,
                title = "Canon Vlogging Kit Series",
                brandName = "Canon India",
                budgetRange = "₹50,000 - ₹90,000",
                type = "Collab",
                platform = "YouTube",
                location = "India",
                durationText = "Ends in 15 days",
                requirements = "• Lifestyle / Travel / Food Vloggers\n• Deliver 1 complete vlog on YouTube\n• Mention specific autofocus & low light attributes\n• Add buying link in target description",
                aboutCampaign = "A promotion campaign for our entry-level vlog cameras designed specifically for creators. Build content that answers: How to start vlogging on a budget.",
                isSaved = false,
                difficultyLevel = "Intermediate",
                category = "Camera Hardware"
            )
        )
        for (op in initialOps) {
            val docData = mapOf(
                "id" to op.id,
                "title" to op.title,
                "brandName" to op.brandName,
                "budgetRange" to op.budgetRange,
                "type" to op.type,
                "platform" to op.platform,
                "location" to op.location,
                "durationText" to op.durationText,
                "requirements" to op.requirements,
                "aboutCampaign" to op.aboutCampaign,
                "isSaved" to op.isSaved,
                "commissionRate" to op.commissionRate,
                "difficultyLevel" to op.difficultyLevel,
                "category" to op.category
            )
            firestore.collection("opportunities").document(op.id.toString()).set(docData)
        }
    }

    fun syncAndListenDeals() {
        val user = firebaseAuth.currentUser
        val uid = user?.uid ?: ""
        firestore.collection("deals").addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot == null || snapshot.isEmpty) {
                seedFirestoreDeals()
            } else {
                val list = mutableListOf<DealEntity>()
                for (doc in snapshot.documents) {
                    try {
                        val idVal = doc.getLong("id")?.toInt() ?: doc.id.hashCode()
                        val opId = doc.getLong("opportunityId")?.toInt() ?: 0
                        val title = doc.getString("title") ?: ""
                        val brandName = doc.getString("brandName") ?: ""
                        val dealAmount = doc.getString("dealAmount") ?: ""
                        val status = doc.getString("status") ?: ""
                        val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        val docUid = doc.getString("uid") ?: ""

                        if (docUid.isEmpty() || docUid == uid) {
                            list.add(
                                DealEntity(
                                    id = idVal,
                                    opportunityId = opId,
                                    title = title,
                                    brandName = brandName,
                                    dealAmount = dealAmount,
                                    status = status,
                                    timestamp = timestamp
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                firestoreDeals.value = list
            }
        }
    }

    private fun seedFirestoreDeals() {
        val user = firebaseAuth.currentUser
        val uid = user?.uid ?: "default_uid"
        val initialDeals = listOf(
            DealEntity(
                id = 1,
                opportunityId = 1,
                title = "Tech Review for YouTube",
                brandName = "boAt Lifestyle",
                dealAmount = "₹50,000",
                status = "In Progress",
                timestamp = System.currentTimeMillis() - 500000
            ),
            DealEntity(
                id = 2,
                opportunityId = 2,
                title = "Promote Canva Pro",
                brandName = "Canva India",
                dealAmount = "₹12,400",
                status = "Payment Released",
                timestamp = System.currentTimeMillis() - 90000000
            )
        )
        for (deal in initialDeals) {
            val docData = mapOf(
                "id" to deal.id,
                "opportunityId" to deal.opportunityId,
                "title" to deal.title,
                "brandName" to deal.brandName,
                "dealAmount" to deal.dealAmount,
                "status" to deal.status,
                "timestamp" to deal.timestamp,
                "uid" to uid
            )
            firestore.collection("deals").document(deal.id.toString()).set(docData)
        }
    }

    // --- FIREBASE AUTH ACTIONS ---
    fun clearAuthError() {
        authError.value = null
    }

    fun setOnboardingSeen() {
        val prefs = getApplication<Application>().getSharedPreferences("creator_hub_profile", Context.MODE_PRIVATE)
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

        if (isLocalDemoMode) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(800)
                authLoading.value = false
                userSessionState.value = UserSessionState.Authenticated(
                    email = email.trim(),
                    uid = "local_demo_uid"
                )
                // Initialize custom profile sprint fields locally
                val profile = loadProfileLocally("local_demo_uid", email.trim()).copy(role = role)
                userProfile.value = profile
                saveProfileLocally(profile)
                syncLocalDataToStateFlows()
                if (role == "brand") {
                    navigateTo("campaign_management")
                } else {
                    navigateTo("opportunities")
                }
            }
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                authLoading.value = false
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid ?: ""
                    fetchOrCreateUserProfile(uid, email.trim())
                } else {
                    authError.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }

    // Legacy support wrapper
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

        if (isLocalDemoMode) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(800)
                authLoading.value = false
                userSessionState.value = UserSessionState.Authenticated(
                    email = email.trim(),
                    uid = "local_demo_uid"
                )
                userProfile.value = profile
                saveProfileLocally(profile)
                syncLocalDataToStateFlows()
                navigateTo("opportunities")
            }
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                authLoading.value = false
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid ?: ""
                    val updatedProfile = profile.copy(uid = uid)
                    userProfile.value = updatedProfile
                    saveProfileLocally(updatedProfile)
                    firestore.collection("users").document(uid).set(updatedProfile)
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

        if (isLocalDemoMode) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(800)
                authLoading.value = false
                userSessionState.value = UserSessionState.Authenticated(
                    email = email.trim(),
                    uid = "local_demo_uid"
                )
                userProfile.value = profile
                saveProfileLocally(profile)
                syncLocalDataToStateFlows()
                navigateTo("campaign_management")
            }
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                authLoading.value = false
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid ?: ""
                    val updatedProfile = profile.copy(uid = uid)
                    userProfile.value = updatedProfile
                    saveProfileLocally(updatedProfile)
                    firestore.collection("users").document(uid).set(updatedProfile)
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

        if (isLocalDemoMode) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(600)
                authLoading.value = false
                authError.value = "Password reset email sent successfully! (Local Demo)"
            }
            return
        }

        firebaseAuth.sendPasswordResetEmail(email.trim())
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
        if (isLocalDemoMode) {
            userSessionState.value = UserSessionState.Unauthenticated
            userProfile.value = null
            navigateTo("login")
            return
        }
        firebaseAuth.signOut()
        navigateTo("login")
    }
}
