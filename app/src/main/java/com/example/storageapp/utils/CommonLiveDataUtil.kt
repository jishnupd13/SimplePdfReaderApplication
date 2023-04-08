package com.example.storageapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object CommonLiveDataUtil {
    val commonListenerMutableLiveData = MutableLiveData<Boolean>()
    var commonListenerLiveData:LiveData<Boolean> = commonListenerMutableLiveData

    val deleteListenerMutableLiveData = MutableLiveData<Boolean>()
    var deleteListenerLiveData:LiveData<Boolean> = deleteListenerMutableLiveData


}