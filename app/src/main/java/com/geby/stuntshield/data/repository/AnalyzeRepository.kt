package com.geby.stuntshield.data.repository

import androidx.lifecycle.liveData
import com.geby.stuntshield.data.ResultState
import com.geby.stuntshield.data.remote.ApiService
import com.geby.stuntshield.data.response.AnalyzeResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException

class AnalyzeRepository private constructor(
    private val apiService: ApiService
) {
    fun analyzeData(gender: String, years: String, months: String, days: String, height: String, weight: String) = liveData {
        emit(ResultState.Loading)
        val genderBody = gender.toRequestBody("text/plain".toMediaType())
        val yearBody = years.toRequestBody("text/plain".toMediaType())
        val monthBody = months.toRequestBody("text/plain".toMediaType())
        val dayBody = days.toRequestBody("text/plain".toMediaType())
        val weightBody = weight.toRequestBody("text/plain".toMediaType())
        val heightBody = height.toRequestBody("text/plain".toMediaType())

        try {
            val successResponse = apiService.analyzeData(yearBody, monthBody, dayBody, genderBody, heightBody, weightBody)
            emit(ResultState.Success(successResponse))
        } catch (e: SocketTimeoutException) {
            emit(ResultState.Error("Kesalahan koneksi jaringan, silahkan coba lagi."))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AnalyzeResponse::class.java)
            emit(ResultState.Error(errorResponse.status?.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: AnalyzeRepository? = null
        fun getInstance(apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: AnalyzeRepository(apiService)
            }.also { instance = it }
    }
}