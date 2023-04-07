package com.example.storageapp.utils

import android.app.Activity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout



fun DrawerLayout.openDrawerDelegate(){
    this.openDrawer(GravityCompat.START)
}

fun DrawerLayout.closeDrawerDelegate(){
    this.closeDrawer(GravityCompat.START)
}

fun DrawerLayout.toggleDrawerDelegate(){
    if(this.isDrawerOpen(GravityCompat.START)){
        this.closeDrawer(GravityCompat.START)
    }else{
        this.openDrawer(GravityCompat.START)
    }
}

fun DrawerLayout.handleBackButtonDelegate(activity:Activity){
    if (this.isDrawerOpen(GravityCompat.START)){
        this.closeDrawer(GravityCompat.START)
    }else{
        activity.finish()
    }
}