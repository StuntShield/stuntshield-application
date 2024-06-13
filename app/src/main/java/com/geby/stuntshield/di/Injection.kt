package com.geby.stuntshield.di

import android.content.Context
import com.geby.stuntshield.data.local.pref.UserPreference
import com.geby.stuntshield.data.local.pref.dataStore
import com.geby.stuntshield.data.remote.ApiConfig
import com.geby.stuntshield.data.repository.AnalyzeRepository

object Injection {
    fun provideRepository(): AnalyzeRepository {
        val apiService = ApiConfig().getApiService()
        return AnalyzeRepository.getInstance(apiService)
    }
}