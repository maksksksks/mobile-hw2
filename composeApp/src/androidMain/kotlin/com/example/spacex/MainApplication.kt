package com.example.spacex

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SpaceXApp.context = applicationContext
        initAndroidContext(SpaceXApp.context)
    }
}