package com.example.storageapp.pdfreader.models

import android.net.Uri

data class PdfModel(
    val id:Long,
    val name:String?="",
    val createdAt:Int,
    val contentUri: Uri,
    val fileSize:Int,
    var isItemSelected:Boolean = false,
    var isEnableSelection:Boolean = false,
    val recentlyViewedTime:Long = 0L
)