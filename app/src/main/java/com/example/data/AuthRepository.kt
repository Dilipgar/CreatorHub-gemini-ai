package com.example.data

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SupabaseProfile(
    val id: String, // UUID
    val role: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

class AuthRepository {
    private val _userSession = MutableStateFlow<UserSessionState>(UserSessionState.Check)
    val userSession: StateFlow<UserSessionState> = _userSession.asStateFlow()

    init {
        // Observe auth state changes locally
        // In full implementation, we can subscribe to Supabase Auth state flow
        checkSession()
    }

    private fun checkSession() {
        val currentUser = SupabaseManager.client.auth.currentUserOrNull()
        if (currentUser != null) {
            _userSession.value = UserSessionState.Authenticated(currentUser.email ?: "", currentUser.id)
        } else {
            _userSession.value = UserSessionState.Unauthenticated
        }
    }

    suspend fun login(email: String, pword: String): Result<String> {
        return try {
            SupabaseManager.client.auth.signInWith(Email) {
                this.email = email
                this.password = pword
            }
            checkSession()
            Result.success("Logged in successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun signup(email: String, pword: String, role: String): Result<String> {
        return try {
            SupabaseManager.client.auth.signUpWith(Email) {
                this.email = email
                this.password = pword
            }
            // Optionally, insert into profiles table
            val currentUser = SupabaseManager.client.auth.currentUserOrNull()
            if (currentUser != null) {
                val profile = SupabaseProfile(id = currentUser.id, role = role)
                SupabaseManager.client.postgrest["profiles"].insert(profile)
            }
            checkSession()
            Result.success("Signed up successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            SupabaseManager.client.auth.signOut()
            checkSession()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

sealed class UserSessionState {
    object Check : UserSessionState()
    object Unauthenticated : UserSessionState()
    data class Authenticated(val email: String, val uid: String) : UserSessionState()
}
