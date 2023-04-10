package com.example.storageapp.pdfreader.search

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.sdk29AndUp
import com.example.storageapp.utils.PdfUtils
import kotlinx.coroutines.flow.flow

class SearchViewModel : ViewModel() {


    fun searchFileByTitle(query:String, context:Context) = flow<List<PdfModel>> {
        val collection = sdk29AndUp {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?:  MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.SIZE
        )

        val files = mutableListOf<PdfModel>()
        val  selection = "_data LIKE '%.pdf' AND title LIKE '%$query%'"

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

                val preview =  PdfUtils.pdfToBitmap(contentUri,context)

                files.add(PdfModel(
                    id = id,
                    name = displayName,
                    fileSize = size,
                    createdAt =  createdAt,
                    contentUri = contentUri,
                    pdfPreview = preview
                ))
            }
            files.toList()
        } ?: listOf()
        emit(files)
    }
}