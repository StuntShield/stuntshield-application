package com.geby.stuntshield.ui.analyze

import androidx.lifecycle.ViewModel
import com.geby.stuntshield.data.repository.AnalyzeRepository

class AnalyzeViewModel(private val repository: AnalyzeRepository) : ViewModel() {
    fun analyzeData(gender: String, years: String, months: String, days: String, weight: String, height: String) =
        repository.analyzeData(gender, years, months, days, height, weight)
}