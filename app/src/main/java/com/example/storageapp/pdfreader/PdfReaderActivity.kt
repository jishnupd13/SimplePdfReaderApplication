package com.example.storageapp.pdfreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.storageapp.databinding.ActivityPdfReaderBinding

class PdfReaderActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPdfReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}