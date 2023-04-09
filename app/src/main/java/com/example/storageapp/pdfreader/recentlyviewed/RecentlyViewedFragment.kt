package com.example.storageapp.pdfreader.recentlyviewed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.fragment.app.viewModels
import com.example.storageapp.R
import com.example.storageapp.adapters.RecentlyViewedAdapter
import com.example.storageapp.databinding.FragmentRecentlyViewedBinding
import com.example.storageapp.pdfreader.documents.DocumentsViewModel
import com.example.storageapp.px
import com.example.storageapp.utils.PdfRecyclerviewItemDecorator
import com.example.storageapp.utils.RecentlyViewedUtils


class RecentlyViewedFragment : Fragment(R.layout.fragment_recently_viewed) {

    private lateinit var binding:FragmentRecentlyViewedBinding
    private val viewModel by viewModels<RecentlyViewedViewModel>()
    private lateinit var adapter: RecentlyViewedAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecentlyViewedBinding.bind(view)
        initViews()
    }

    private fun initViews(){
        binding.apply {
            adapter = RecentlyViewedAdapter()
            recyclerviewRecentlyViewed.adapter = adapter
            recyclerviewRecentlyViewed.addItemDecoration(PdfRecyclerviewItemDecorator(paddingTop = 14.px, paddingHorizontal = 14.px, paddingBottom = 8.px))
        }
        observeRecentlyViewedItems()
        viewModel.fetchRecentlyViewedItems()
    }

    private fun observeRecentlyViewedItems(){
        viewModel.recentlyViewedLiveData.observe(viewLifecycleOwner){
            adapter.asyncListDiffer.submitList(it)
        }
    }

}