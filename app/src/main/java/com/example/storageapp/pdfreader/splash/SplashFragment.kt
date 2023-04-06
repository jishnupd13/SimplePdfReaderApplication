package com.example.storageapp.pdfreader.splash

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storageapp.*
import com.example.storageapp.databinding.FragmentSplashBinding


class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var binding:FragmentSplashBinding
    private val viewModel by viewModels<SplashViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashBinding.bind(view)
        motionLayoutTransitionListener()
        observeReadAndWritePermissionLiveData()
    }

    private fun observeReadAndWritePermissionLiveData(){
        viewModel.permissionLiveDate.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToPermissionInfoFragment())
            }else{
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
            }
        }
    }

    private fun motionLayoutTransitionListener(){
        binding.motionLayout.addTransitionListener(object :MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
               //TODO to be implemented
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                //TODO to be implemented
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                viewModel.checkPermissionNeeded(requireContext())
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                //TODO to be implemented
            }
        })
    }

}