package com.chirag_redij.lister.di

import android.app.Application
import android.content.Context
import com.chirag_redij.lister.presentation.notes.NotesClient
import com.chirag_redij.lister.presentation.notes.NotesRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
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

@Module
@InstallIn(ActivityComponent::class)
object NotesModule {

    @Provides
    @ActivityScoped
    fun provideNotesClient(notesRepo: NotesRepo): NotesClient {
        return NotesClient(notesRepo)
    }

    @Provides
    @ActivityScoped
    fun provideNotesRepo(): NotesRepo = NotesRepo()
}