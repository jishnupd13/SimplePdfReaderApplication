package com.example.storageapp.pdfreader.documents

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.sdk29AndUp
import kotlinx.coroutines.launch

class DocumentsViewModel(application: Application):AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private var _documentsMutableLiveData = MutableLiveData<List<PdfModel>>()
    val documentsLiveData:LiveData<List<PdfModel>> = _documentsMutableLiveData

    fun fetchPdfDocumentsFromExternalStorage(context:Context) = viewModelScope.launch {
        val collection = sdk29AndUp {
            MediaStore.Files.getContentUri("external_primary")
        } ?:  MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.SIZE
        )

        val files = mutableListOf<PdfModel>()
        val  selection = "_data LIKE '%.pdf'"

         context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
            val sizeOfFileColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val createdAtColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

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

    init {
        fetchPdfDocumentsFromExternalStorage(context)
    }

}