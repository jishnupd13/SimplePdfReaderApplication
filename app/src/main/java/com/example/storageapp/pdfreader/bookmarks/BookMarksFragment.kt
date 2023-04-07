package com.example.storageapp.pdfreader.bookmarks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.storageapp.R
import com.example.storageapp.databinding.FragmentBookMarksBinding


class BookMarksFragment : Fragment(R.layout.fragment_book_marks) {

    private lateinit var binding:FragmentBookMarksBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookMarksBinding.bind(view)
    }

}