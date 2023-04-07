package com.example.storageapp.pdfreader.recentlyviewed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.storageapp.R
import com.example.storageapp.databinding.FragmentRecentlyViewedBinding


class RecentlyViewedFragment : Fragment(R.layout.fragment_recently_viewed) {

    private lateinit var binding:FragmentRecentlyViewedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecentlyViewedBinding.bind(view)
    }

}