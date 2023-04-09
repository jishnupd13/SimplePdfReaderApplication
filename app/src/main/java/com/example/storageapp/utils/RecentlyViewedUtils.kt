package com.example.storageapp.utils

import com.example.storageapp.pdfreader.models.PdfModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class RecentlyViewedUtils {

    fun saveRecentlyViewedItem(item:PdfModel){
        val currentList = getRecentlyViewedList()
        currentList.add(item)
        val gson = Gson()
        val json: String = gson.toJson(currentList)
        PreferenceUtils.saveRecentlyViewedItem(json)
    }

    private fun getRecentlyViewedList():ArrayList<PdfModel>{
        try {
            val jsonString = PreferenceUtils.getRecentlyViewedItem()
            if (jsonString.isEmpty())
                return arrayListOf()
            val gson = Gson()
            val type = object : TypeToken<java.util.ArrayList<PdfModel?>?>() {}.type
            return gson.fromJson(jsonString, type)
        }catch (e:Exception){
            return arrayListOf()
        }
    }
}