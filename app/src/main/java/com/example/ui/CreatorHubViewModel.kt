package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreatorHubViewModel(application: Application) : AndroidViewModel(application) {
    val repository: CreatorHubRepository = CreatorHubRepository(AppDatabase.getDatabase(application))
    var firebaseAuth: FirebaseAuth
    var firestore: FirebaseFirestore
    var isLocalDemoMode = true

    // Instantiate sub-viewmodels (which will take care of real business logic)
    val authViewModel: AuthViewModel = AuthViewModel(this, application)
    val profileViewModel: ProfileViewModel = ProfileViewModel(this, application)
    val opportunityViewModel: OpportunityViewModel = OpportunityViewModel(this, application)
    val affiliateViewModel: AffiliateViewModel = AffiliateViewModel(this, application)

    // Auth delegation properties and methods
    var currentScreen: MutableStateFlow<String>
        get() = authViewModel.currentScreen
        set(value) { authViewModel.currentScreen.value = value.value }

    val userSessionState get() = authViewModel.userSessionState
    val selectedRole get() = authViewModel.selectedRole
    val authError get() = authViewModel.authError
    val authLoading get() = authViewModel.authLoading

    fun navigateTo(screen: String) = authViewModel.navigateTo(screen)
    fun clearAuthError() = authViewModel.clearAuthError()
    fun setOnboardingSeen() = authViewModel.setOnboardingSeen()
    fun login(email: String, password: String, role: String) = authViewModel.login(email, password, role)
    fun login(email: String, password: String) = authViewModel.login(email, password)
    fun signupCreator(name: String, email: String, password: String, instagram: String, youtube: String, category: String) =
        authViewModel.signupCreator(name, email, password, instagram, youtube, category)
    fun signupBrand(companyName: String, email: String, password: String, website: String, industry: String) =
        authViewModel.signupBrand(companyName, email, password, website, industry)
    fun signup(email: String, password: String) = authViewModel.signup(email, password)
    fun forgotPassword(email: String) = authViewModel.forgotPassword(email)
    fun logout() = authViewModel.logout()

    // Profile delegation properties and methods
    val userProfile get() = profileViewModel.userProfile
    val notifications get() = profileViewModel.notifications

    fun fetchOrCreateUserProfile(uid: String, email: String) = profileViewModel.fetchOrCreateUserProfile(uid, email)
    fun saveProfileLocally(profile: UserProfile) = profileViewModel.saveProfileLocally(profile)
    fun loadProfileLocally(uid: String, email: String) = profileViewModel.loadProfileLocally(uid, email)
    fun updateUserProfile(profile: UserProfile) = profileViewModel.updateUserProfile(profile)

    // Opportunity delegation properties and methods
    val selectedOpportunityId get() = opportunityViewModel.selectedOpportunityId
    val activeChatSessionId get() = opportunityViewModel.activeChatSessionId
    val chatSessions get() = opportunityViewModel.chatSessions
    val activeDeals get() = opportunityViewModel.activeDeals
    val allOpportunities get() = opportunityViewModel.allOpportunities
    val filteredOpportunities get() = opportunityViewModel.filteredOpportunities
    val selectedOpportunity get() = opportunityViewModel.selectedOpportunity
    val selectedOpportunityDeal get() = opportunityViewModel.selectedOpportunityDeal
    val chatMessages get() = opportunityViewModel.chatMessages
    val firestoreOpportunities get() = opportunityViewModel.firestoreOpportunities
    val firestoreDeals get() = opportunityViewModel.firestoreDeals
    val searchQuery get() = opportunityViewModel.searchQuery
    val activeTabFilter get() = opportunityViewModel.activeTabFilter
    val selectedIndustry get() = opportunityViewModel.selectedIndustry
    val selectedBudgetRange get() = opportunityViewModel.selectedBudgetRange

    fun selectOpportunity(id: Int) = opportunityViewModel.selectOpportunity(id)
    fun selectChatSession(id: Int) = opportunityViewModel.selectChatSession(id)
    fun setSearchQuery(query: String) = opportunityViewModel.setSearchQuery(query)
    fun setActiveTab(tab: String) = opportunityViewModel.setActiveTab(tab)
    fun setSelectedIndustry(industry: String) = opportunityViewModel.setSelectedIndustry(industry)
    fun setSelectedBudgetRange(budget: String) = opportunityViewModel.setSelectedBudgetRange(budget)
    fun toggleSaveOpportunity(op: OpportunityEntity) = opportunityViewModel.toggleSaveOpportunity(op)
    fun applyForOpportunity(opId: Int, title: String, brandName: String, proposedBudget: String) =
        opportunityViewModel.applyForOpportunity(opId, title, brandName, proposedBudget)
    fun advanceDealStatus(deal: DealEntity) = opportunityViewModel.advanceDealStatus(deal)
    fun sendMessageToActiveChat(text: String) = opportunityViewModel.sendMessageToActiveChat(text)
    fun createCustomOpportunity(
        title: String,
        brandName: String,
        budgetRange: String,
        type: String,
        platform: String,
        requirements: String,
        aboutCampaign: String
    ) = opportunityViewModel.createCustomOpportunity(title, brandName, budgetRange, type, platform, requirements, aboutCampaign)

    // Affiliate delegation properties and methods
    val selectedAffiliateOfferId get() = affiliateViewModel.selectedAffiliateOfferId
    val selectedAffiliateOffer get() = affiliateViewModel.selectedAffiliateOffer
    val allAffiliateOffers get() = affiliateViewModel.allAffiliateOffers
    val affiliateEarnings get() = affiliateViewModel.affiliateEarnings
    val affiliateSearchQuery get() = affiliateViewModel.affiliateSearchQuery
    val affiliateCategoryFilter get() = affiliateViewModel.affiliateCategoryFilter
    val filteredAffiliateOffers get() = affiliateViewModel.filteredAffiliateOffers

    fun selectAffiliateOffer(id: Int) = affiliateViewModel.selectAffiliateOffer(id)
    fun setAffiliateSearchQuery(query: String) = affiliateViewModel.setAffiliateSearchQuery(query)
    fun setAffiliateCategoryFilter(category: String) = affiliateViewModel.setAffiliateCategoryFilter(category)
    fun toggleSaveAffiliateOffer(id: Int) = affiliateViewModel.toggleSaveAffiliateOffer(id)
    fun applyToAffiliateOffer(id: Int, brandName: String) = affiliateViewModel.applyToAffiliateOffer(id, brandName)
    fun simulateClickOnAffiliate(offerId: Int) = affiliateViewModel.simulateClickOnAffiliate(offerId)

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

        initListeners(application)
    }

    private fun initListeners(application: Application) {
        if (isLocalDemoMode) {
            opportunityViewModel.syncLocalDataToStateFlows()
            profileViewModel.userProfile.value = profileViewModel.loadProfileLocally("local_demo_uid", "dilip.goswami96@gmail.com")
            profileViewModel.notifications.value = listOf(
                NotificationItem(1, "Welcome to CreatorHub!", "Complete your profile edit sprint to start landing verified escrow deals.", "10 mins ago", "system", false),
                NotificationItem(2, "Escrow Secured", "boAt Lifestyle reviewed your portfolio and locked ₹50,000 in safe escrow.", "1 hour ago", "deal", false),
                NotificationItem(3, "Campaign Trending", "GoPro Action Shorts is seeing high application rates in Travel category.", "1 day ago", "campaign", false)
            )
            authViewModel.userSessionState.value = UserSessionState.Unauthenticated
            val prefs = application.getSharedPreferences("creator_hub_profile", Context.MODE_PRIVATE)
            val hasSeenOnboarding = prefs.getBoolean("seen_onboarding", false)
            if (!hasSeenOnboarding) {
                authViewModel.currentScreen.value = "onboarding"
            } else {
                if (authViewModel.currentScreen.value != "signup" && authViewModel.currentScreen.value != "forgot_password") {
                    authViewModel.currentScreen.value = "login"
                }
            }
        } else {
            // Sync opportunities immediately
            opportunityViewModel.syncAndListenOpportunities()

            // Listen to Firebase Auth state changes
            firebaseAuth.addAuthStateListener { auth ->
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    authViewModel.userSessionState.value = UserSessionState.Authenticated(
                        email = firebaseUser.email ?: "",
                        uid = firebaseUser.uid
                    )
                    // Fetch or automatically initialize user profile in Firestore
                    profileViewModel.fetchOrCreateUserProfile(firebaseUser.uid, firebaseUser.email ?: "")
                    // Listen to deals associated with the user
                    opportunityViewModel.syncAndListenDeals()
                    
                    if (authViewModel.currentScreen.value == "login" || authViewModel.currentScreen.value == "signup" || authViewModel.currentScreen.value == "forgot_password") {
                        authViewModel.currentScreen.value = "opportunities"
                    }
                } else {
                    authViewModel.userSessionState.value = UserSessionState.Unauthenticated
                    profileViewModel.userProfile.value = null
                    opportunityViewModel.firestoreDeals.value = emptyList()
                    if (authViewModel.currentScreen.value != "signup" && authViewModel.currentScreen.value != "forgot_password") {
                        authViewModel.currentScreen.value = "login"
                    }
                }
            }
        }
    }
}
