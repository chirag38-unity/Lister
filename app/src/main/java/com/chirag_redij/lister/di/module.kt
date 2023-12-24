package com.chirag_redij.lister.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.chirag_redij.lister.presentation.sign_in.GoogleAuthUIClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object module {

    @Provides
    @ActivityScoped
    @ActivityContext
    fun provideActivityContext(activity: AppCompatActivity): Context {
        return activity
    }

    @Provides
    @Singleton
    fun provideGoogleAuthUIClient(@ApplicationContext context: Context): GoogleAuthUIClient =
        GoogleAuthUIClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

}