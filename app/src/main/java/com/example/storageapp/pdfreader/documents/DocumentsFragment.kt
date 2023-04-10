package com.example.storageapp.pdfreader.documents

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.RecoverableSecurityException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.storageapp.R
import com.example.storageapp.adapters.PdfItemAdapter
import com.example.storageapp.databinding.FragmentDocumentsBinding
import com.example.storageapp.hide
import com.example.storageapp.pdfreader.home.HomeFragment
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.px
import com.example.storageapp.show
import com.example.storageapp.utils.CommonLiveDataUtil
import com.example.storageapp.utils.PdfRecyclerviewItemDecorator
import com.example.storageapp.utils.RecentlyViewedUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class DocumentsFragment : Fragment(R.layout.fragment_documents) {

    private lateinit var binding:FragmentDocumentsBinding
    private val viewModel by viewModels<DocumentsViewModel>()
    private lateinit var adapter: PdfItemAdapter
    private var navHostFragment: NavHostFragment?=null
    private var parent:HomeFragment?=null
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    private var list = arrayListOf<Uri>()
    private var recentlyViewedUtils = RecentlyViewedUtils()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDocumentsBinding.bind(view)
        navHostFragment = parentFragment as NavHostFragment?
        parent = navHostFragment?.parentFragment as HomeFragment
        observeCommonListenerLiveData()
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

          intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if(it.resultCode == RESULT_OK) {
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    lifecycleScope.launch {
                        deleteFiles(list)
                    }
                }
                Toast.makeText(requireContext(), "Photo deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Photo couldn't be deleted", Toast.LENGTH_SHORT).show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
           handleOnBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                @SuppressLint("NotifyDataSetChanged")
                override fun handleOnBackPressed() {
                    if(viewModel.isEnableSelectionItem.value == true){
                        parent?.hideSelection()
                        viewModel.itemSelectionCount.value = 0
                        viewModel.isEnableSelectionItem.value = false
                        adapter.asyncListDiffer.currentList.map {
                            it.isItemSelected = false
                            it.isEnableSelection = false
                        }
                        adapter.notifyDataSetChanged()
                    }else
                        requireActivity().finish()
                }
            }
        )
    }

    fun deleteFiles(){
        adapter.asyncListDiffer.currentList.map {
            if(it.isItemSelected)
                list.add(it.contentUri)
        }

        if(list.isNotEmpty()){
            lifecycleScope.launch {
                deleteFiles(list)
            }
            viewModel.itemSelectionCount.value = 0
            parent?.hideSelection()
            viewModel.isEnableSelectionItem.value = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItemCountEmpty(){
        viewModel.itemSelectionCount.value = 0
        adapter.asyncListDiffer.currentList.map {
            it.isItemSelected = false
            it.isEnableSelection= false
        }
        adapter.notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun observeCommonListenerLiveData(){
        CommonLiveDataUtil.commonListenerLiveData.observe(viewLifecycleOwner){
           adapter.asyncListDiffer.currentList.map {
               it.isItemSelected = false
               it.isEnableSelection = false
           }
           adapter.notifyDataSetChanged()
           parent?.hideSelection()
            viewModel.itemSelectionCount.value = 0
            viewModel.isEnableSelectionItem.value = false
        }
    }

    private fun initViews() {
        val onLongItemClicked = { items:PdfModel,position:Int ->
            if(items.isItemSelected){
                items.isItemSelected = false
                adapter.notifyItemChanged(position)
                decrementItemCount()
            }else{
                items.isItemSelected = true
                adapter.notifyItemChanged(position)
                incrementItemCount()
            }
            parent?.showSelection()
            viewModel.isEnableSelectionItem.value = true
        }

        val onItemClicked = { item:PdfModel,position:Int ->
            if(item.isEnableSelection){
                if(item.isItemSelected){
                    item.isItemSelected = false
                    adapter.notifyItemChanged(position)
                    decrementItemCount()
                }else{
                    item.isItemSelected = true
                    adapter.notifyItemChanged(position)
                    incrementItemCount()
                }
            }else{
                recentlyViewedUtils.saveRecentlyViewedItem(item)
                findNavController().navigate(DocumentsFragmentDirections.actionDocumentsToPdfViewerFragment())
            }

        }

        binding.apply {
            adapter = PdfItemAdapter(onItemLongPressed = onLongItemClicked, onItemClick = onItemClicked)
            recyclerviewPdf.itemAnimator = null
            recyclerviewPdf.adapter = adapter
            recyclerviewPdf.addItemDecoration(PdfRecyclerviewItemDecorator(paddingTop = 14.px, paddingHorizontal = 14.px, paddingBottom = 8.px))
        }
        viewModel.fetchPdfDocumentsFromExternalStorage(requireContext())
    }


    private fun incrementItemCount(){
        viewModel.itemSelectionCount.value = viewModel.itemSelectionCount.value?.plus(1)
        parent?.setSelectedItemText(viewModel.itemSelectionCount.value!!)
    }

    private fun decrementItemCount(){
        viewModel.itemSelectionCount.value = viewModel.itemSelectionCount.value?.minus(1)
        if(viewModel.itemSelectionCount.value!! >=0)
            parent?.setSelectedItemText(viewModel.itemSelectionCount.value!!)
        else{
            viewModel.itemSelectionCount.value = 0
            parent?.setSelectedItemText(viewModel.itemSelectionCount.value!!)
        }
    }

    private suspend fun deleteFiles(list: ArrayList<Uri>){
        withContext( Dispatchers.IO) {
            try {
                for (i in list){
                    requireContext().contentResolver.delete(i, null, null)
                }
                viewModel.fetchPdfDocumentsFromExternalStorage(requireContext())
            }catch (e:Exception){
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(requireContext().contentResolver, list).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException = e as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }

                intentSender?.let { sender ->
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }
    }

}