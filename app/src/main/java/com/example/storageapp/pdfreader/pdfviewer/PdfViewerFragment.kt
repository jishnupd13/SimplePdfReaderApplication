package com.example.storageapp.pdfreader.pdfviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.storageapp.R
import com.example.storageapp.databinding.FragmentPdfViewerBinding


class PdfViewerFragment : Fragment(R.layout.fragment_pdf_viewer) {

    private lateinit var binding:FragmentPdfViewerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPdfViewerBinding.bind(view)
    }

}