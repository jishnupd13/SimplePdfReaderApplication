package com.example.storageapp.pdfreader.bookmarks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Adapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storageapp.R
import com.example.storageapp.adapters.BookMarkItemsAdapter
import com.example.storageapp.databinding.FragmentBookMarksBinding
import com.example.storageapp.hide
import com.example.storageapp.pdfreader.recentlyviewed.RecentlyViewedViewModel
import com.example.storageapp.px
import com.example.storageapp.show
import com.example.storageapp.utils.PdfRecyclerviewItemDecorator


class BookMarksFragment : Fragment(R.layout.fragment_book_marks) {

    private lateinit var binding:FragmentBookMarksBinding
    private lateinit var adapter: BookMarkItemsAdapter
    private val viewModel by viewModels<BookMarksViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookMarksBinding.bind(view)
        initViews()
        viewModel.fetchBookMarkList(requireContext())
    }

    private fun initViews(){
        binding.apply {
            adapter = BookMarkItemsAdapter(){
                findNavController().navigate(BookMarksFragmentDirections.actionBookMarksToPdfViewerFragment(it))
            }
            recyclerViewBookMarks.adapter = adapter
            recyclerViewBookMarks.addItemDecoration(PdfRecyclerviewItemDecorator(paddingTop = 14.px, paddingHorizontal = 14.px, paddingBottom = 8.px))
        }
        observeBookmarkItems()
    }

    private fun observeBookmarkItems(){
        viewModel.bookMarkItemsLiveData.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                binding.layoutPdfNoFilesFound.show()
                binding.recyclerViewBookMarks.hide()
                binding.layoutPdfNoFilesFound.animation = AnimationUtils.loadAnimation(requireContext(),R.anim.shake_anim)
            }else{
                binding.layoutPdfNoFilesFound.hide()
                binding.recyclerViewBookMarks.show()
                adapter.asyncListDiffer.submitList(it)
            }
        }
    }

}