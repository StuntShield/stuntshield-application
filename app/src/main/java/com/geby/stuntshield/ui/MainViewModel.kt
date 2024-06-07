package com.geby.stuntshield.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.geby.stuntshield.data.local.pref.UserPreference
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

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}