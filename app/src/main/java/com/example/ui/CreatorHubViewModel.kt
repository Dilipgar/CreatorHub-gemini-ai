package com.example.ui

import android.app.Application
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
    private val repository: CreatorHubRepository
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

    val userSessionState = MutableStateFlow<UserSessionState>(UserSessionState.Check)

    var authError = MutableStateFlow<String?>(null)
        private set

    var authLoading = MutableStateFlow(false)
        private set

    // Firestore-backed StateFlows
    val firestoreOpportunities = MutableStateFlow<List<OpportunityEntity>>(emptyList())
    val firestoreDeals = MutableStateFlow<List<DealEntity>>(emptyList())
    val userProfile = MutableStateFlow<UserProfile?>(null)

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
        repository = CreatorHubRepository(database)

        if (isLocalDemoMode) {
            syncLocalDataToStateFlows()
            userSessionState.value = UserSessionState.Unauthenticated
            if (currentScreen.value != "signup" && currentScreen.value != "forgot_password") {
                currentScreen.value = "login"
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

    // Exposed Flows (Mapped directly to Firestore data!)
    val allOpportunities: StateFlow<List<OpportunityEntity>> = firestoreOpportunities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatSessions: StateFlow<List<ChatSessionEntity>> = repository.chatSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDeals: StateFlow<List<DealEntity>> = firestoreDeals
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

    // Stream for selected opportunity derived through Firestore opportunities
    val selectedOpportunity: StateFlow<OpportunityEntity?> = selectedOpportunityId
        .combine(firestoreOpportunities) { id, ops ->
            ops.find { it.id == id }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Stream for selected opportunity's deal state derived through Firestore deals (applied or in progress)
    val selectedOpportunityDeal: StateFlow<DealEntity?> = selectedOpportunityId
        .combine(firestoreDeals) { id, deals ->
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
                // Update in Firestore!
                firestore.collection("deals").document(deal.id.toString())
                    .update("status", nextStatus)
            }
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
            val newId = (firestoreOpportunities.value.maxOfOrNull { it.id } ?: 0) + 1
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
            }

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
                            activeContractsCount = activeContractsCount
                        )
                        userProfile.value = profile
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

    fun updateUserProfile(profile: UserProfile) {
        val uid = profile.uid.takeIf { it.isNotEmpty() } ?: return
        userProfile.value = profile
        if (!isLocalDemoMode) {
            firestore.collection("users").document(uid).set(profile)
        }
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

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            authError.value = "Email and Password cannot be empty"
            return
        }
        authLoading.value = true
        authError.value = null

        if (isLocalDemoMode) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(800)
                authLoading.value = false
                userSessionState.value = UserSessionState.Authenticated(
                    email = email.trim(),
                    uid = "local_demo_uid"
                )
                // Initialize default profile locally
                userProfile.value = UserProfile(
                    uid = "local_demo_uid",
                    email = email.trim(),
                    name = "Ankit Photographer",
                    handle = "@" + email.substringBefore("@")
                )
                syncLocalDataToStateFlows()
                navigateTo("opportunities")
            }
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                authLoading.value = false
                if (task.isSuccessful) {
                    navigateTo("opportunities")
                } else {
                    authError.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }

    fun signup(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            authError.value = "Email and Password cannot be empty"
            return
        }
        if (password.length < 6) {
            authError.value = "Password should be at least 6 characters"
            return
        }
        authLoading.value = true
        authError.value = null

        if (isLocalDemoMode) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(800)
                authLoading.value = false
                userSessionState.value = UserSessionState.Authenticated(
                    email = email.trim(),
                    uid = "local_demo_uid"
                )
                // Initialize default profile locally
                userProfile.value = UserProfile(
                    uid = "local_demo_uid",
                    email = email.trim(),
                    name = "Ankit Photographer",
                    handle = "@" + email.substringBefore("@")
                )
                syncLocalDataToStateFlows()
                navigateTo("opportunities")
            }
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                authLoading.value = false
                if (task.isSuccessful) {
                    navigateTo("opportunities")
                } else {
                    authError.value = task.exception?.localizedMessage ?: "Signup failed"
                }
            }
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
