package com.chirag_redij.lister.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object module {

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application) : Context {
        return application.applicationContext
    }

}