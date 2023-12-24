package com.chirag_redij.lister

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        Timber.plant(Timber.DebugTree())
        super.onCreate()
    }

}