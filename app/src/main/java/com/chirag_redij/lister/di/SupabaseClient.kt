package com.chirag_redij.lister.di

import com.chirag_redij.lister.BuildConfig
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseKey = BuildConfig.supabaseKey,
        supabaseUrl = BuildConfig.supabaseUrl
    ) {
        install(Auth){
            host = "www.lister.com"
            scheme = "https"

        }
        install(ComposeAuth) {
            googleNativeLogin(serverClientId = BuildConfig.googleClientId)
        }
        install(Postgrest){
        }
        install(Realtime){
            connectOnSubscribe = true
        }
    }

}

