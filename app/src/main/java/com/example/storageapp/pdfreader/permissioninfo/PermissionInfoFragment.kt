package com.example.storageapp.pdfreader.permissioninfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storageapp.R
import com.example.storageapp.databinding.FragmentPermissionInfoBinding
import com.example.storageapp.pdfreader.splash.SplashViewModel
import com.example.storageapp.utils.StatusBarDelegate


class PermissionInfoFragment : Fragment(R.layout.fragment_permission_info) {

    private lateinit var binding:FragmentPermissionInfoBinding
    private val viewModel by viewModels<SplashViewModel>()
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPermissionInfoBinding.bind(view)
        StatusBarDelegate().showStatusBar(activity = requireActivity())
        permissionLauncher()
        observeReadAndWritePermissionLiveData()
        initViews()
    }

    private fun initViews(){
        binding.apply {
            btnAllowPermission.setOnClickListener {
                viewModel.checkPermissionNeeded(requireContext())
            }
        }
    }

    private fun permissionLauncher(){
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val granted = it.value
                val permission = it.key
                if (!granted) {
                    val neverAskAgain = !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)
                    if (neverAskAgain) {
                        //user click "never ask again"
                        openAppSettings()
                    } else {
                        //show explain dialog
                        viewModel.checkPermissionNeeded(requireContext())
                    }
                    return@registerForActivityResult
                }else{
                    findNavController().navigate(PermissionInfoFragmentDirections.actionPermissionInfoFragmentToHomeFragment())
                }
            }
        }
    }

    private fun openAppSettings(){
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        requireActivity().startActivity(intent)
    }


    private fun observeReadAndWritePermissionLiveData(){
        viewModel.permissionLiveDate.observe(viewLifecycleOwner){
            permissionsLauncher.launch(it.toTypedArray())
        }
    }
}