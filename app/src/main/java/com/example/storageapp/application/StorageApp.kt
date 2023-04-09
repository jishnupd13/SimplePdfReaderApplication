package com.example.storageapp.application

import android.app.Application
import com.example.storageapp.utils.PreferenceUtils

class StorageApp: Application() {

    override fun onCreate() {
        super.onCreate()
        PreferenceUtils.initPreference(this)
        PreferenceUtils.saveValue("dummy","dumb")
    }
}