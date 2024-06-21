package com.geby.stuntshield.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.geby.stuntshield.data.repository.AnalyzeRepository
import com.geby.stuntshield.di.Injection
import com.geby.stuntshield.ui.analyze.AnalyzeViewModel

class ViewModelFactory(
    private val analyzeRepository: AnalyzeRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AnalyzeViewModel::class.java) -> {
                AnalyzeViewModel(analyzeRepository) as T
            }
            // Add other ViewModel classes here
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository()
                )
            }.also { instance = it }
        }
    }
}
