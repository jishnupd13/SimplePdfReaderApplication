package com.example.storageapp.pdfreader.documents

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.storageapp.*
import com.example.storageapp.adapters.PdfItemAdapter
import com.example.storageapp.databinding.FragmentDocumentsBinding
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.utils.PdfRecyclerviewItemDecorator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DocumentsFragment : Fragment(R.layout.fragment_documents) {

    private lateinit var binding:FragmentDocumentsBinding
    private val viewModel by viewModels<DocumentsViewModel>()
    private lateinit var adapter: PdfItemAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDocumentsBinding.bind(view)
        initViews()
        viewModel.documentsLiveData.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                adapter.asyncListDiffer.submitList(it)
                binding.layoutPdfNoFilesFound.hide()
            }
            else {
                binding.layoutPdfNoFilesFound.show()
                binding.layoutPdfNoFilesFound.animation = AnimationUtils.loadAnimation(requireContext(),R.anim.shake_anim)
            }
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