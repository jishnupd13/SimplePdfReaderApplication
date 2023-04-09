package com.example.storageapp.pdfreader.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.storageapp.R
import com.example.storageapp.databinding.FragmentHomeBinding
import com.example.storageapp.hide
import com.example.storageapp.pdfreader.documents.DocumentsFragment
import com.example.storageapp.show
import com.example.storageapp.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding:FragmentHomeBinding
    private var nestedNavHostFragment:NavHostFragment? = null
    private var navController:NavController?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        initViews()
        Log.e("data", PreferenceUtils.getValue("dummy"))
    }

    private fun initViews() {
        binding.apply {

             nestedNavHostFragment = childFragmentManager.findFragmentById(R.id.navHostFragment) as? NavHostFragment
             navController = nestedNavHostFragment?.navController

            navController.let {

                bottomNavigationView.setupWithNavController(navController!!)
                navController?.addOnDestinationChangedListener { controller, destination, arguments ->

                    when(destination.id) {

                        R.id.documents->{
                            layoutToolBar.show()
                            hideSelection()
                        }
                        R.id.recentlyViewed->{
                            layoutToolBar.show()
                            hideSelection()
                        }
                        R.id.bookMarks->{
                            layoutToolBar.show()
                            hideSelection()
                        }
                        R.id.pdfViewerFragment->{
                            layoutToolBar.hide()
                        }
                    }
                }
            }

            imgDelete.setOnClickListener {
               // CommonLiveDataUtil.deleteListenerMutableLiveData.postValue(true)

            }

            imageOptions.setOnClickListener {
                drawerLayout.openDrawerDelegate()
            }

            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        drawerLayout.handleBackButtonDelegate(requireActivity())
                    }
                }
            )

            navigationView.setNavigationItemSelectedListener { menuItem->
                when(menuItem.itemId) {
                    R.id.home -> {

                        bottomNavigationView.selectedItemId = R.id.documents
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(100)
                            drawerLayout.closeDrawerDelegate()
                        }
                    }
                    else->{
                        throw NoSuchElementException("Invalid Item")
                    }
                }
                true
            }

            imgDelete.setOnClickListener {
                val fragment =nestedNavHostFragment?.childFragmentManager?.primaryNavigationFragment
                if (fragment is DocumentsFragment){
                    fragment.deleteFiles()
                }
            }

            imgClose.setOnClickListener {
                hideSelection()
                val fragment =nestedNavHostFragment?.childFragmentManager?.primaryNavigationFragment
                if (fragment is DocumentsFragment){
                    fragment.setItemCountEmpty()
                }
            }
        }
    }

     fun showSelection(){
       binding.apply {
           layoutSelectedItems.show()
           layoutControls.hide()
       }
    }

    fun hideSelection(){
        binding.apply {
            layoutControls.show()
            layoutSelectedItems.hide()
        }
    }

    @SuppressLint("SetTextI18n")
     fun setSelectedItemText(selectedItemCount:Int) = binding.apply {
        when (selectedItemCount) {
            0 -> textSelection.text = "No items selected"
            1 -> textSelection.text = "$selectedItemCount item selected"
            else -> textSelection.text = "$selectedItemCount items selected"
        }
    }


}