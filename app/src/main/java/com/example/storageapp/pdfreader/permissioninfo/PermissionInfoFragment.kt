package com.example.storageapp.pdfreader.permissioninfo

import android.app.Dialog
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.storageapp.R
import com.example.storageapp.databinding.AlertOpenSettingsPermissionBinding
import com.example.storageapp.databinding.FragmentPermissionInfoBinding
import com.example.storageapp.pdfreader.splash.SplashViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PermissionInfoFragment : Fragment(R.layout.fragment_permission_info) {

    private lateinit var binding:FragmentPermissionInfoBinding
    private val viewModel by viewModels<SplashViewModel>()
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

     private var settingsPermissionResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
         lifecycleScope.launch(Dispatchers.Main) {
             delay(600)
             viewModel.checkPermissionNeeded(requireContext())
         }
     }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPermissionInfoBinding.bind(view)
        //StatusBarDelegate().showStatusBar(activity = requireActivity())
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
                        showOpenSettingsDialog()
                    } else {
                        //show explain dialog
                        viewModel.checkPermissionNeeded(requireContext())
                    }
                    return@registerForActivityResult
                }
                if(permissions.entries.last() == it){
                    findNavController().navigate(PermissionInfoFragmentDirections.actionPermissionInfoFragmentToHomeFragment())
                }
            }

        }
    }

    private fun openAppSettings(){
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        with(intent) {
            data = Uri.fromParts("package", requireContext().packageName, null)
            addCategory(CATEGORY_DEFAULT)
        }
        settingsPermissionResultLauncher.launch(intent)
    }


    private fun observeReadAndWritePermissionLiveData(){
        viewModel.permissionLiveDate.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                findNavController().navigate(PermissionInfoFragmentDirections.actionPermissionInfoFragmentToHomeFragment())
            }else{
                permissionsLauncher.launch(it.toTypedArray())
            }
        }
    }

    private fun showOpenSettingsDialog(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val binding  = AlertOpenSettingsPermissionBinding .inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
        binding.btnOpenSettings.setOnClickListener{
        dialog.dismiss()
        openAppSettings()
        }
    }
}