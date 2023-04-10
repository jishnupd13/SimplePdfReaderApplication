package com.example.storageapp.pdfreader.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.storageapp.*
import com.example.storageapp.adapters.SearchAdapter
import com.example.storageapp.databinding.FragmentSearchBinding
import com.example.storageapp.utils.PdfRecyclerviewItemDecorator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding:FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var adapter:SearchAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        initViews()
    }

    private fun initViews(){
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                   findNavController().navigateUp()
                }
            }
        )
        setUpSearchStateFlow()
        binding.apply {
            adapter = SearchAdapter()
            recyclerviewSearch.addItemDecoration(PdfRecyclerviewItemDecorator(paddingTop = 14.px, paddingHorizontal = 14.px, paddingBottom = 8.px))
            recyclerviewSearch.itemAnimator = null
            recyclerviewSearch.adapter = adapter
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun setUpSearchStateFlow() {
        lifecycleScope.launch {
            binding.searchView.getQueryTextChangeStateFlow()
                .debounce(300)
                .filter { query ->
                    withContext(Dispatchers.Main){
                        binding.textNoResultFound.hide()
                    }
                    if (query.isEmpty()) {
                        adapter.asyncListDiffer.submitList(arrayListOf())
                        return@filter false
                    } else {
                        return@filter true
                    }
                }
                .distinctUntilChanged()
                .flatMapLatest { query->
                    viewModel.searchFileByTitle(query,requireContext()).
                    catch {
                        Log.e("exception","${it.message}")
                        emit(listOf())
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect{
                    adapter.asyncListDiffer.submitList(it)
                    if(it.isEmpty()){
                        binding.textNoResultFound.show()
                    }else{
                        binding.textNoResultFound.hide()
                    }
                }
        }
    }
}