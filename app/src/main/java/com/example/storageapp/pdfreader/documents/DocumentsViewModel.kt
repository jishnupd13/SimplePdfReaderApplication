package com.example.storageapp.pdfreader.documents

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.sdk29AndUp
import kotlinx.coroutines.launch


class DocumentsViewModel:ViewModel() {

    private var _documentsMutableLiveData = MutableLiveData<List<PdfModel>>()
    val documentsLiveData:LiveData<List<PdfModel>> = _documentsMutableLiveData

    var itemSelectionCount = MutableLiveData<Int>().apply {
        value = 0
    }

    var isEnableSelectionItem = MutableLiveData<Boolean>().apply {
        value = false
    }

    fun fetchPdfDocumentsFromExternalStorage(context:Context) = viewModelScope.launch {
        val collection = sdk29AndUp {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?:  MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.SIZE
        )

        val pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")

        val files = mutableListOf<PdfModel>()
        val  selection = "_data LIKE '%.pdf'"
       // val  selection = "${MediaStore.Files.FileColumns.MIME_TYPE} =?"

         context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Files.FileColumns.TITLE} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
            val sizeOfFileColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val createdAtColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)

            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val size = cursor.getInt(sizeOfFileColumn)
                val createdAt = cursor.getInt(createdAtColumn)
                val contentUri = ContentUris.appendId(
                    MediaStore.Files.getContentUri("external").buildUpon(),
                    id
                ).build()

                files.add(PdfModel(
                    id = id,
                    name = displayName,
                    fileSize = size,
                    createdAt =  createdAt,
                    contentUri = contentUri
                ))
            }
            files.toList()
        } ?: listOf()

        Log.e("pdf","${files.size}")
        _documentsMutableLiveData.value = files
    }
}