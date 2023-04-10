package com.example.storageapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type


typealias bookMarkIdList = ArrayList<Long>

class BookMarkUtils {

    fun fetchBookMarkIdList() : bookMarkIdList{
        try {
            val jsonString = PreferenceUtils.getBookMarkIds()
            if (jsonString.isEmpty())
                return arrayListOf()

            val gson = Gson()
            val type = object : TypeToken<java.util.ArrayList<Long?>?>() {}.type
            return gson.fromJson(jsonString, type)
        }catch (e:Exception){
            Log.e("exception","${e.message}")
            return arrayListOf()
        }
    }

    fun saveBookMarkId(id:Long) {
        val currentList = fetchBookMarkIdList()
        if(!currentList.contains(id)){
            currentList.add(id)
            val gson =  Gson()
            val json: String = gson.toJson(currentList)
            PreferenceUtils.saveBookMarkIds(json)
        }
    }

    fun removeBookMarkId(id:Long){
        val currentList = fetchBookMarkIdList()
        if(currentList.contains(id) && currentList.isNotEmpty()){
            currentList.remove(id)
            val gson = Gson()
            val json: String = gson.toJson(currentList)
            PreferenceUtils.saveBookMarkIds(json)
        }
    }


}