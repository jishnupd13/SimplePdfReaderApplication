package com.example.storageapp.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import java.io.File


object PdfUtils {

    fun pdfToBitmap(uri:Uri,context: Context) : Bitmap?{

        var bitmap:Bitmap ?= null
        try {
            val file = getPath(uri,context)?.let { File(it) }
            val renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
            val pageCount = renderer.pageCount
            if(pageCount > 0){
                val page = renderer.openPage(0)
                val width = page.width
                val height = page.height
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                renderer.close()
            }

        }catch (e:Exception){
            Log.e("error","${e.message}")
            e.printStackTrace()
        }
        return bitmap
    }


   private fun getPath(uri: Uri,context: Context): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            context.contentResolver.query(uri, projection, null, null, null) ?: return null
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s: String = cursor.getString(columnIndex)
        cursor.close()
        return s
    }
}