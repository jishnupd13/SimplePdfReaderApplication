package com.example.storageapp.pdfreader.documents

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.storageapp.R
import com.example.storageapp.adapters.PdfItemAdapter
import com.example.storageapp.databinding.FragmentDocumentsBinding
import com.example.storageapp.dp
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.px
import com.example.storageapp.utils.PdfRecyclerviewItemDecorator

class DocumentsFragment : Fragment(R.layout.fragment_documents) {

    private lateinit var binding:FragmentDocumentsBinding
    private val viewModel by viewModels<DocumentsViewModel>()
    private lateinit var adapter: PdfItemAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDocumentsBinding.bind(view)
        initViews()
        viewModel.documentsLiveData.observe(viewLifecycleOwner){
            adapter.asyncListDiffer.submitList(it)
        }

    }

    private fun initViews() {
        val onLongItemClicked = { items:PdfModel,position:Int ->
            if(items.isItemSelected){
                items.isItemSelected = false
                adapter.notifyItemChanged(position)
            }else{
                items.isItemSelected = true
                adapter.notifyItemChanged(position)
            }
        }

        binding.apply {
            adapter = PdfItemAdapter(onItemLongPressed = onLongItemClicked)
            recyclerviewPdf.adapter = adapter
            recyclerviewPdf.addItemDecoration(PdfRecyclerviewItemDecorator(paddingTop = 14.px, paddingHorizontal = 14.px, paddingBottom = 8.px))
        }
    }
}