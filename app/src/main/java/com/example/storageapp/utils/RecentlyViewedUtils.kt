package com.example.storageapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.storageapp.pdfreader.models.PdfModel
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
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
        val gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriTypeAdapter)
            .registerTypeAdapter(Bitmap::class.java,BitmapTypeAdapter)
            .create()
        val json: String = gson.toJson(currentList)
        PreferenceUtils.saveRecentlyViewedItem(json)
    }

     fun getRecentlyViewedList():pdfList{
        try {
            val jsonString = PreferenceUtils.getRecentlyViewedItem()
            if (jsonString.isEmpty())
                return arrayListOf()
            val gson =  GsonBuilder()
                .registerTypeAdapter(Uri::class.java, UriTypeAdapter)
                .registerTypeAdapter(Bitmap::class.java,BitmapTypeAdapter)
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

object BitmapTypeAdapter : JsonDeserializer<Bitmap>, JsonSerializer<Bitmap> {
    private fun bitMapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

   private fun stringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            Log.e("hii","hii ${e.message}")
            e.message
            null
        }
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Bitmap {
        return stringToBitMap(json?.asString.toString())!!
    }

    override fun serialize(
        src: Bitmap?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(bitMapToString(src!!))
    }
}
