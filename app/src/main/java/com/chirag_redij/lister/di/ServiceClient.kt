package com.chirag_redij.lister.di

import android.graphics.Color
import com.chirag_redij.lister.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.ExternalAuthAction
import io.github.jan.supabase.gotrue.FlowType

object ServiceClient {

    val serviceClient = createSupabaseClient(
        supabaseKey = BuildConfig.supabaseServiceKey,
        supabaseUrl = BuildConfig.supabaseUrl
    ) {
        install(Auth) {
            host = "www.lister.com"
            scheme = "https"
            flowType = FlowType.PKCE
            defaultExternalAuthAction = ExternalAuthAction.CustomTabs(
                intentBuilder = {
                    this.setShowTitle(true)
                    setToolbarColor(Color.RED)
                }
            )
        }
    }

}