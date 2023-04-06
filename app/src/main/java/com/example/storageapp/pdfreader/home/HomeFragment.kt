package com.example.storageapp.pdfreader.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.storageapp.R
import com.example.storageapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding:FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
    }

}