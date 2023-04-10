package com.example.storageapp.pdfreader.bookmarks

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.sdk29AndUp
import com.example.storageapp.utils.BookMarkUtils
import com.example.storageapp.utils.PdfUtils
import kotlinx.coroutines.launch

class BookMarksViewModel:ViewModel() {

    private val bookMarkUtils = BookMarkUtils()

    private val bookMarkItemsMutableLiveData = MutableLiveData<List<PdfModel>>()
    val bookMarkItemsLiveData :LiveData<List<PdfModel>> = bookMarkItemsMutableLiveData

    fun fetchBookMarkList(context:Context) = viewModelScope.launch {
        val collection = sdk29AndUp {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?:  MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.SIZE
        )

            // val pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")

        val files = mutableListOf<PdfModel>()
        val  selection = "_data LIKE '%.pdf'"
        val bookMarkedIdList = bookMarkUtils.fetchBookMarkIdList()
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

                val thumbnail =  PdfUtils.pdfToBitmap(contentUri,context)

                files.add(PdfModel(
                    id = id,
                    name = displayName,
                    fileSize = size,
                    createdAt =  createdAt,
                    contentUri = contentUri,
                    pdfPreview = thumbnail
                ))
            }
            files.toList()
        } ?: listOf()

        bookMarkItemsMutableLiveData.value = files.filter { bookMarkedIdList.contains(it.id) }
    }
}