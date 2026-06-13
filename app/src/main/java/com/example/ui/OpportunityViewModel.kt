package com.example.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OpportunityViewModel(
    private val parent: CreatorHubViewModel,
    private val application: Application
) {
    // Firestore-backed StateFlows
    val firestoreOpportunities = MutableStateFlow<List<OpportunityEntity>>(emptyList())
    val firestoreDeals = MutableStateFlow<List<DealEntity>>(emptyList())

    // Search and tab filters
    val searchQuery = MutableStateFlow("")
    val activeTabFilter = MutableStateFlow("All") // "All", "Brand Deals", "Affiliate", "Collab", "Contest"
    val selectedIndustry = MutableStateFlow("All")
    val selectedBudgetRange = MutableStateFlow("Any")

    val selectedOpportunityId = MutableStateFlow<Int?>(null)
    val activeChatSessionId = MutableStateFlow<Int?>(null)

    // Exposed Flows (Mapped directly to repository for 100% reliable local stability & offline persistence!)
    val allOpportunities: StateFlow<List<OpportunityEntity>> = parent.repository.opportunities
        .stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatSessions: StateFlow<List<ChatSessionEntity>> = parent.repository.chatSessions
        .stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDeals: StateFlow<List<DealEntity>> = parent.repository.activeDeals
        .stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Opportunities based on Search Query, Tab, Industry & Budget Range
    val filteredOpportunities: StateFlow<List<OpportunityEntity>> = combine(
        allOpportunities,
        searchQuery,
        activeTabFilter,
        selectedIndustry,
        selectedBudgetRange
    ) { ops, query, tab, industry, budget ->
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

            val matchesIndustry = if (industry == "All") {
                true
            } else {
                op.category.contains(industry, ignoreCase = true)
            }

            val matchesBudget = when (budget) {
                "Any" -> true
                "High Fixed (₹25k+)" -> {
                    val numbersOnly = op.budgetRange.replace(Regex("[^0-9]"), "")
                    val numVal = numbersOnly.toIntOrNull() ?: 0
                    numVal >= 25000 || op.budgetRange.contains("25,000") || op.budgetRange.contains("40,000") || op.budgetRange.contains("50,000") || op.budgetRange.contains("90,000") || op.budgetRange.contains("75,000")
                }
                "Commission/Affiliate" -> {
                    op.budgetRange.contains("Commission", ignoreCase = true) || op.budgetRange.contains("Sales", ignoreCase = true) || op.budgetRange.contains("Recurring", ignoreCase = true) || op.budgetRange.contains("rate", ignoreCase = true)
                }
                "Paid Collaboration" -> {
                    op.budgetRange.contains("Paid", ignoreCase = true) || op.budgetRange.contains("₹", ignoreCase = true)
                }
                else -> true
            }

            matchesQuery && matchesTab && matchesIndustry && matchesBudget
        }
    }.stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stream for selected opportunity derived through local repository opportunities
    val selectedOpportunity: StateFlow<OpportunityEntity?> = selectedOpportunityId
        .combine(allOpportunities) { id, ops ->
            ops.find { it.id == id }
        }.stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Stream for selected opportunity's deal state derived through local repository deals (applied or in progress)
    val selectedOpportunityDeal: StateFlow<DealEntity?> = selectedOpportunityId
        .combine(activeDeals) { id, deals ->
            deals.find { it.opportunityId == id }
        }.stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Stream of messages for selected chat (using local cache Room repository)
    val chatMessages: StateFlow<List<MessageEntity>> = activeChatSessionId
        .flatMapLatest { id ->
            if (id != null) parent.repository.getMessages(id) else flowOf(emptyList())
        }.stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun syncLocalDataToStateFlows() {
        parent.viewModelScope.launch {
            parent.repository.opportunities.collect { list ->
                firestoreOpportunities.value = list
            }
        }
        parent.viewModelScope.launch {
            parent.repository.activeDeals.collect { list ->
                firestoreDeals.value = list
            }
        }
    }

    fun selectOpportunity(id: Int) {
        selectedOpportunityId.value = id
        parent.authViewModel.navigateTo("details")
    }

    fun selectChatSession(id: Int) {
        activeChatSessionId.value = id
        parent.authViewModel.navigateTo("chat_detail")
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setActiveTab(tab: String) {
        activeTabFilter.value = tab
    }

    fun setSelectedIndustry(industry: String) {
        selectedIndustry.value = industry
    }

    fun setSelectedBudgetRange(budget: String) {
        selectedBudgetRange.value = budget
    }

    fun toggleSaveOpportunity(op: OpportunityEntity) {
        parent.viewModelScope.launch {
            val nextSaved = !op.isSaved
            parent.repository.updateOpportunity(op.copy(isSaved = nextSaved))
            if (!parent.isLocalDemoMode) {
                // Save to Firestore!
                parent.firestore.collection("opportunities").document(op.id.toString())
                    .update("isSaved", nextSaved)
                    .addOnFailureListener {
                        // fall back or ignore
                    }
            }
        }
    }

    // Apply to opportunity
    fun applyForOpportunity(opId: Int, title: String, brandName: String, proposedBudget: String) {
        parent.viewModelScope.launch {
            parent.repository.applyToOpportunity(opId, title, brandName, proposedBudget)
            
            if (!parent.isLocalDemoMode) {
                // Save to Firestore!
                val user = parent.firebaseAuth.currentUser
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
                parent.firestore.collection("deals").document(newId.toString()).set(docData)
            }

            // Get the details of the opportunity or use defaults
            val currentOp = selectedOpportunity.value
            val opBrief = currentOp?.aboutCampaign ?: "Deliverables include social posts and product walkthrough"
            val creatorName = parent.profileViewModel.userProfile.value?.name ?: "Dhruv clicks"

            // Generate pitch using Gemini! (asynchronous non-blocking)
            val generatedPitch = com.example.data.GeminiService.generatePitch(
                creatorName = creatorName,
                opportunityTitle = title,
                brandName = brandName,
                proposedBudget = proposedBudget,
                opportunityDetails = opBrief
            )

            // Auto-create a local message in prototype chat with the Gemini pitch!
            parent.repository.startChatSession(brandName, currentOp?.platform ?: "YouTube", generatedPitch)
        }
    }

    // Advances the deal's standard flow inside Firestore and Room
    fun advanceDealStatus(deal: DealEntity) {
        parent.viewModelScope.launch {
            val nextStatus = when (deal.status) {
                "Applied" -> "Accepted" // Brand accepts, adds funds
                "Accepted" -> "Work Submitted" // Creator does work
                "Work Submitted" -> "Under Review" // Brand reviews
                "Under Review" -> "Completed" // Brand approves
                "Completed" -> "Payment Released" // Escrow releases
                else -> "Applied" // Reset for demo lifecycle loop
            }
            parent.repository.updateDealStatus(deal.id, nextStatus)

            if (!parent.isLocalDemoMode) {
                try {
                    // Update in Firestore!
                    parent.firestore.collection("deals").document(deal.id.toString())
                        .update("status", nextStatus)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Post a custom deal escrow milestone notification
            val newNotification = NotificationItem(
                id = (parent.profileViewModel.notifications.value.maxOfOrNull { it.id } ?: 0) + 1,
                title = "Milestone: $nextStatus",
                body = "Your escrow contract with ${deal.brandName} is now in status '$nextStatus'.",
                timestamp = "Just now",
                type = "deal"
            )
            parent.profileViewModel.notifications.value = listOf(newNotification) + parent.profileViewModel.notifications.value
        }
    }

    fun sendMessageToActiveChat(text: String) {
        val sId = activeChatSessionId.value ?: return
        if (text.isBlank()) return
        parent.viewModelScope.launch {
            parent.repository.sendMessage(sId, text, "creator")
            
            // Auto-respond for a fun, interactive prototype experience!
            simulateBrandReply(sId, text)
        }
    }

    private fun simulateBrandReply(sessionId: Int, creatorMsg: String) {
        parent.viewModelScope.launch {
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
            parent.repository.sendMessage(sessionId, responseText, "brand")
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
        parent.viewModelScope.launch {
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
            parent.repository.insertOpportunity(op)

            if (!parent.isLocalDemoMode) {
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
                    parent.firestore.collection("opportunities").document(op.id.toString()).set(docData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Post a system notification about campaign publication
            val newNotification = NotificationItem(
                id = (parent.profileViewModel.notifications.value.maxOfOrNull { it.id } ?: 0) + 1,
                title = "Campaign Published Successful!",
                body = "Your custom campaign '$title' for $brandName was successfully published.",
                timestamp = "Just now",
                type = "campaign"
            )
            parent.profileViewModel.notifications.value = listOf(newNotification) + parent.profileViewModel.notifications.value

            parent.authViewModel.navigateTo("opportunities")
        }
    }

    fun syncAndListenOpportunities() {
        parent.firestore.collection("opportunities").addSnapshotListener { snapshot, error ->
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
            parent.firestore.collection("opportunities").document(op.id.toString()).set(docData)
        }
    }

    fun syncAndListenDeals() {
        val user = parent.firebaseAuth.currentUser
        val uid = user?.uid ?: ""
        parent.firestore.collection("deals").addSnapshotListener { snapshot, error ->
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
        val user = parent.firebaseAuth.currentUser
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
            parent.firestore.collection("deals").document(deal.id.toString()).set(docData)
        }
    }
}
