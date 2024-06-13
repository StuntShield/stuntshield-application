package com.geby.stuntshield.data.remote

import com.geby.stuntshield.data.response.AnalyzeResponse
import com.geby.stuntshield.data.response.ArticleResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("stunting-classification")
    suspend fun analyzeData(
        @Part("year") year: RequestBody,
        @Part("month") month: RequestBody,
        @Part("day") day: RequestBody,
        @Part("jenis_kelamin") jenis_kelamin: RequestBody,
        @Part("tinggi_badan") tinggi_badan: RequestBody,
        @Part("berat_badan") berat_badan: RequestBody
    ): AnalyzeResponse

    @GET("articles")
    fun getArticles(): Call<ArticleResponse>
}