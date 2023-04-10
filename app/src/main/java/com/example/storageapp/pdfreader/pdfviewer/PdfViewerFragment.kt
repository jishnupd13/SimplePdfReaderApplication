package com.example.storageapp.pdfreader.pdfviewer

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.storageapp.R
import com.example.storageapp.databinding.FragmentPdfViewerBinding
import com.example.storageapp.pdfreader.models.PdfModel


class PdfViewerFragment : Fragment(R.layout.fragment_pdf_viewer) {

    private lateinit var binding:FragmentPdfViewerBinding
    private val args :PdfViewerFragmentArgs by navArgs()
    private var pdfModel:PdfModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPdfViewerBinding.bind(view)
        pdfModel = args.item
        Log.e("item","<< ${pdfModel?.contentUri}")
        if(pdfModel?.contentUri!=null){
            binding.textPdfTitle.text = pdfModel?.name?:""
            val targetFilePath = getPath(pdfModel?.contentUri!!)?:""
            Log.e("path","<< $targetFilePath")
            binding.activityMainPdfView
                .fromFile(targetFilePath)
                .show()
        }
        binding.imageBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            requireContext().contentResolver.query(uri, projection, null, null, null) ?: return null
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s: String = cursor.getString(columnIndex)
        cursor.close()
        return s
    }
}