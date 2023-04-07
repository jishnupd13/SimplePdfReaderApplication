package com.example.storageapp.pdfreader.home

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.storageapp.R
import com.example.storageapp.databinding.FragmentHomeBinding
import com.example.storageapp.utils.closeDrawerDelegate
import com.example.storageapp.utils.handleBackButtonDelegate
import com.example.storageapp.utils.openDrawerDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding:FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        initViews()
    }

    private fun initViews() {

        val nestedNavHostFragment = childFragmentManager.findFragmentById(R.id.navHostFragment) as? NavHostFragment
        val navController = nestedNavHostFragment?.navController
        if(navController!=null)
            binding.bottomNavigationView.setupWithNavController(navController)
        else
            throw RuntimeException("run time exception")

        binding.imageOptions.setOnClickListener {
            binding.drawerLayout.openDrawerDelegate()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.drawerLayout.handleBackButtonDelegate(requireActivity())
                }
            }
        )

        binding.navigationView.setNavigationItemSelectedListener { menuItem->
            when(menuItem.itemId) {
                R.id.home -> {

                    binding.bottomNavigationView.selectedItemId = R.id.documents
                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(200)
                        binding.drawerLayout.closeDrawerDelegate()
                    }
                   // binding.bottomNavigationView.selectedItemId = R.id.documents
                }
                else->{
                    throw NoSuchElementException("Invalid Item")
                }
            }
            true
        }
    }


}