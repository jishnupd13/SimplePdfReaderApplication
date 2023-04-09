package com.example.storageapp.pdfreader.recentlyviewed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.utils.RecentlyViewedUtils
import kotlinx.coroutines.launch

class RecentlyViewedViewModel:ViewModel() {

    private var recentlyViewedMutableLiveData = MutableLiveData<List<PdfModel>>()
    var recentlyViewedLiveData:LiveData<List<PdfModel>> = recentlyViewedMutableLiveData

    fun fetchRecentlyViewedItems() = viewModelScope.launch {
        recentlyViewedMutableLiveData.postValue(RecentlyViewedUtils().getRecentlyViewedList().sortedByDescending {
            it.recentlyViewedTime
        })
    }
}