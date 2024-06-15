package com.geby.stuntshield.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.geby.stuntshield.data.local.pref.UserPreference
import com.geby.stuntshield.data.response.UserData
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference) : ViewModel() {

    fun getUserId(): LiveData<String> {
        return pref.getUserId().asLiveData()
    }

    fun saveUserId(userId: String) {
        viewModelScope.launch {
            pref.saveUserId(userId)
        }

    }
    fun saveUserData(userName: String, email: String, photoUrl: String) {
        viewModelScope.launch {
            pref.saveUserData(userName, email, photoUrl)
        }
    }

    fun getUserData(): LiveData<UserData> {
        return pref.getUserData().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.clearUserData()
            pref.logout()
        }
    }


}