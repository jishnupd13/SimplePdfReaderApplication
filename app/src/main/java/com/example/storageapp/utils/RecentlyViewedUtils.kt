package com.example.storageapp.utils

import android.net.Uri
import com.example.storageapp.pdfreader.models.PdfModel
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


typealias pdfList = ArrayList<PdfModel>

class RecentlyViewedUtils {
    fun saveRecentlyViewedItem(item:PdfModel){
        val currentList = getRecentlyViewedList()
        var position = -1
        val isItemFound = currentList.any {
            if(it.id==item.id){ position = currentList.indexOf(it)}
            it.id == item.id
        }
        if (isItemFound){
            if (position>=0)
                currentList[position].recentlyViewedTime = System.currentTimeMillis()
        }else{
            item.recentlyViewedTime = System.currentTimeMillis()
            currentList.add(item)
        }
        val gson = GsonBuilder().registerTypeAdapter(Uri::class.java, UriTypeAdapter)
            .create()
        val json: String = gson.toJson(currentList)
        PreferenceUtils.saveRecentlyViewedItem(json)
    }

     fun getRecentlyViewedList():pdfList{
        try {
            val jsonString = PreferenceUtils.getRecentlyViewedItem()
            if (jsonString.isEmpty())
                return arrayListOf()
            val gson =  GsonBuilder().registerTypeAdapter(Uri::class.java, UriTypeAdapter)
                .create()
            val type = object : TypeToken<ArrayList<PdfModel?>?>() {}.type
            return gson.fromJson(jsonString, type)
        }catch (e:Exception){
            return arrayListOf()
        }
    }
}

object UriTypeAdapter : JsonDeserializer<Uri>, JsonSerializer<Uri> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Uri {
        return Uri.parse(json?.asString.toString())
    }

    override fun serialize(src: Uri?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }
}
