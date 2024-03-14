package com.chirag_redij.lister.di

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
//import com.chirag_redij.lister.presentation.sign_in.GoogleAuthUIClient
import com.google.android.gms.auth.api.identity.Identity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import javax.inject.Singleton

import com.chirag_redij.lister.BuildConfig
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

@Module
@InstallIn(SingletonComponent::class)
object module {

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application) : Context {
        return application.applicationContext
    }

}