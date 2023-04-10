package com.example.storageapp.pdfreader.recentlyviewed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storageapp.R
import com.example.storageapp.adapters.RecentlyViewedAdapter
import com.example.storageapp.databinding.FragmentRecentlyViewedBinding
import com.example.storageapp.hide
import com.example.storageapp.px
import com.example.storageapp.show
import com.example.storageapp.utils.PdfRecyclerviewItemDecorator


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
            adapter = RecentlyViewedAdapter(){
                findNavController().navigate(RecentlyViewedFragmentDirections.actionRecentlyViewedToPdfViewerFragment(it))
            }
            recyclerviewRecentlyViewed.adapter = adapter
            recyclerviewRecentlyViewed.itemAnimator = null
            recyclerviewRecentlyViewed.addItemDecoration(PdfRecyclerviewItemDecorator(paddingTop = 14.px, paddingHorizontal = 14.px, paddingBottom = 8.px))
        }
        observeRecentlyViewedItems()
        viewModel.fetchRecentlyViewedItems()
    }

    private fun observeRecentlyViewedItems(){
        viewModel.recentlyViewedLiveData.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                binding.recyclerviewRecentlyViewed.hide()
                binding.layoutPdfNoFilesFound.show()
                binding.layoutPdfNoFilesFound.animation = AnimationUtils.loadAnimation(requireContext(),R.anim.shake_anim)
            }else{
                binding.recyclerviewRecentlyViewed.show()
                binding.layoutPdfNoFilesFound.hide()
                adapter.asyncListDiffer.submitList(it)
            }
        }
    }

}