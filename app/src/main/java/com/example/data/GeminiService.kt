package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.concurrent.TimeUnit

// Moshi data mapping for Gemini request/response
data class GeminiPart(val text: String)
data class GeminiContent(val parts: List<GeminiPart>)
data class GeminiRequest(val contents: List<GeminiContent>)

data class GeminiCandidate(val content: GeminiContent?)
data class GeminiResponse(val candidates: List<GeminiCandidate>?)

object GeminiService {
    private const val TAG = "GeminiService"
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Call the Gemini API to generate a personalized pitch.
     * Fallback to a solid formatted default text if GEMINI_API_KEY is placeholder or empty,
     * or if the network call fails.
     */
    suspend fun generatePitch(
        creatorName: String,
        opportunityTitle: String,
        brandName: String,
        proposedBudget: String,
        opportunityDetails: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey.length < 10) {
            Log.e(TAG, "Gemini API key is not configured or is a placeholder. Using intelligent local generator.")
            return@withContext "Hi $brandName Team,\n\nI am extremely excited about the '$opportunityTitle' opportunity! I've spent time reviewing your campaign brief and would love to collaborate on producing high-converting Content.\n\nMy proposed commercial rate is $proposedBudget based on the deliverables. Let's schedule a message call to align on details!"
        }

        val prompt = """
            You are a professional social media content creator/influencer named '$creatorName' applying for a brand deal.
            Brand Name: '$brandName'
            Campaign/Opportunity: '$opportunityTitle'
            Details of campaign: '$opportunityDetails'
            My Bid Budget: '$proposedBudget'
            
            Write a highly engaging, professional, and personalized 3-sentence elevator pitch application message sent to the brand to pitch why they should accept my bid and collaborate with me. Be direct, enthusiastic, polished, and do not use generic AI greetings or placeholders. Output only the message text itself.
        """.trimIndent()

        try {
            val requestBodyObj = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )
            
            val jsonAdapter = moshi.adapter(GeminiRequest::class.java)
            val jsonString = jsonAdapter.toJson(requestBodyObj)
            
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val reqBody = jsonString.toRequestBody(mediaType)
            
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(reqBody)
                .build()
                
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string() ?: ""
                    Log.e(TAG, "Request failed: code=${response.code} body=$errorBody")
                    throw Exception("API returned unsuccessful code ${response.code}")
                }
                
                val respBodyString = response.body?.string() ?: ""
                val responseAdapter = moshi.adapter(GeminiResponse::class.java)
                val responseObj = responseAdapter.fromJson(respBodyString)
                
                val text = responseObj?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (text != null) {
                    text.trim()
                } else {
                    throw Exception("Failed to parse choice text from response")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during generatePitch call", e)
            "Hi $brandName Team,\n\nI am extremely excited about the '$opportunityTitle' opportunity! I've spent time reviewing your campaign brief and would love to collaborate on producing high-converting Content.\n\nMy proposed commercial rate is $proposedBudget based on the deliverables. Let's schedule a message call to align on details!"
        }
    }
}
