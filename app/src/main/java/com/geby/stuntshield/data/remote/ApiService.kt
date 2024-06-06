package com.geby.stuntshield.data.remote

import com.geby.stuntshield.data.response.AnalyzeResponse
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("prediction")
    suspend fun analyzeData(
        @Part("month") month: RequestBody,
        @Part("day") day: RequestBody,
        @Part("jenis_kelamin") jenis_kelamin: RequestBody,
        @Part("tinggi_badan") tinggi_badan: RequestBody
    ): AnalyzeResponse
}