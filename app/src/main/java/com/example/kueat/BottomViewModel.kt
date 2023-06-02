package com.example.kueat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BottomViewModel:ViewModel() {
    val selectedBottom = MutableLiveData<Int>()
    fun setLiveData(num:Int){
        selectedBottom.value = num
    }
}