package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        OpportunityEntity::class,
        ChatSessionEntity::class,
        MessageEntity::class,
        DealEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun opportunityDao(): OpportunityDao
    abstract fun chatDao(): ChatDao
    abstract fun dealDao(): DealDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "creatorhub_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seeds the database on first run
            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)
                populateInitialData(database)
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val opDao = db.opportunityDao()
            val chatDao = db.chatDao()
            val dealDao = db.dealDao()

            // 1. Seed Opportunities
            val ops = listOf(
                OpportunityEntity(
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
            opDao.insertAll(ops)

            // 2. Seed Chat Sessions & Messages
            val session1Id = chatDao.insertSession(
                ChatSessionEntity(
                    partnerName = "boAt Lifestyle",
                    platformType = "YouTube",
                    lastMessage = "Hi Ankit, we loved your work! Looking forward to reviewing the final draft.",
                    timestamp = System.currentTimeMillis() - 600000
                )
            ).toInt()

            chatDao.insertMessage(MessageEntity(chatSessionId = session1Id, sender = "brand", messageText = "Hi Ankit, we loved your portfolio! We think you're a perfect fit for our upcoming Tech Review campaign.", timestamp = System.currentTimeMillis() - 3600000))
            chatDao.insertMessage(MessageEntity(chatSessionId = session1Id, sender = "creator", messageText = "Awesome! Thanks for reaching out. I would love to collaborate. What are the key requirements for the review?", timestamp = System.currentTimeMillis() - 1800000))
            chatDao.insertMessage(MessageEntity(chatSessionId = session1Id, sender = "brand", messageText = "Hi Ankit, we loved your work! Looking forward to reviewing the final draft.", timestamp = System.currentTimeMillis() - 600000))

            val session2Id = chatDao.insertSession(
                ChatSessionEntity(
                    partnerName = "GoPro India",
                    platformType = "Instagram",
                    lastMessage = "Let's collaborate for the upcoming trail expedition! Please share your shipping address.",
                    timestamp = System.currentTimeMillis() - 86400000
                )
            ).toInt()

            chatDao.insertMessage(MessageEntity(chatSessionId = session2Id, sender = "creator", messageText = "Hi team GoPro! Extremely excited about this campaign. I shoot all my extreme trails on GoPro anyways!", timestamp = System.currentTimeMillis() - 96400000))
            chatDao.insertMessage(MessageEntity(chatSessionId = session2Id, sender = "brand", messageText = "Let's collaborate for the upcoming trail expedition! Please share your shipping address.", timestamp = System.currentTimeMillis() - 86400000))

            val session3Id = chatDao.insertSession(
                ChatSessionEntity(
                    partnerName = "Canon India",
                    platformType = "YouTube",
                    lastMessage = "Partnership opportunity has been updated. Check details in the contract tab.",
                    timestamp = System.currentTimeMillis() - 172800000
                )
            ).toInt()

            chatDao.insertMessage(MessageEntity(chatSessionId = session3Id, sender = "brand", messageText = "Partnership opportunity has been updated. Check details in the contract tab.", timestamp = System.currentTimeMillis() - 172800000))

            // 3. Seed Initial Deals (e.g. Boat Tech Review is In Progress, Canva is Completed, etc.)
            dealDao.insertDeal(
                DealEntity(
                    opportunityId = 1,
                    title = "Tech Review for YouTube",
                    brandName = "boAt Lifestyle",
                    dealAmount = "₹50,000",
                    status = "In Progress",
                    timestamp = System.currentTimeMillis() - 500000
                )
            )
            dealDao.insertDeal(
                DealEntity(
                    opportunityId = 2,
                    title = "Promote Canva Pro",
                    brandName = "Canva India",
                    dealAmount = "₹12,400",
                    status = "Payment Released",
                    timestamp = System.currentTimeMillis() - 90000000
                )
            )
        }
    }
}
