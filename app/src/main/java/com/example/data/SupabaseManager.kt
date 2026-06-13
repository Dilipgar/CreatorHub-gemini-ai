package com.example.data

import com.example.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime

object SupabaseManager {
    val client: SupabaseClient by lazy {
        val supabaseUrl = BuildConfig.SUPABASE_URL ?: ""
        val supabaseKey = BuildConfig.SUPABASE_KEY ?: ""
        
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Postgrest)
            install(Auth)
            install(Realtime)
        }
    }
}
