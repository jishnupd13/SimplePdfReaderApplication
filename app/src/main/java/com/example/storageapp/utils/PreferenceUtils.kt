package com.example.storageapp.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

object PreferenceUtils {

    private var preferenceInstance : SharedPreferences? = null
    private var preferenceEditor:Editor? = null
    private var recentlyViewedItemTag = "recentlyViewed"

    fun initPreference(context: Context){
        if (preferenceInstance == null){
           preferenceInstance =  context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
            preferenceEditor = preferenceInstance?.edit()
        }
    }

    fun saveValue(key:String,value:String){
        preferenceEditor?.apply {
            putString(key,value)
            commit()
        }
    }

    fun getValue(key: String):String{
        return preferenceInstance?.getString(key,"")?:""
    }


    fun saveRecentlyViewedItem(value:String){
        preferenceEditor?.apply {
            putString(recentlyViewedItemTag,value)
            commit()
        }
    }

    fun getRecentlyViewedItem():String{
        return preferenceInstance?.getString(recentlyViewedItemTag,"")?:""
    }
}