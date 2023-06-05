package com.example.kueat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kueat.`object`.User

class MyUserModel: ViewModel() {
    val selectedUser = MutableLiveData<User>()
    fun setLiveData(user:User){
        selectedUser.value = user
    }
}