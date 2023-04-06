package com.example.storageapp.utils

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager

class StatusBarDelegate {

    //To hide the status bar and enter in to full screen
    @Suppress("DEPRECATION")
     fun hideStatusBar(activity:Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    //To show the status bar
    @Suppress("DEPRECATION")
    fun showStatusBar (activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.show(WindowInsets.Type.statusBars())
        }else{
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

}