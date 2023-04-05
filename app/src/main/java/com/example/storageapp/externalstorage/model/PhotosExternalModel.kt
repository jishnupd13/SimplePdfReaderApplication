package com.example.storageapp.externalstorage.model

import android.net.Uri

data class PhotosExternalModel(
    val id:Long,
    val name:String,
    val width:Int,
    val height:Int,
    val contentUri:Uri
)