package com.geby.stuntshield.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geby.stuntshield.BuildConfig
import com.geby.stuntshield.data.remote.ApiConfig
import com.geby.stuntshield.data.response.ArticleResponse
import com.geby.stuntshield.data.response.ResultsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _listArticle = MutableLiveData<List<ResultsItem>>()
    val listArticle: LiveData<List<ResultsItem>> = _listArticle

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    companion object{
        private const val TAG = "HomeViewModel"
    }

    init {
        showArticleList()
    }

    fun showArticleList() {
        _isLoading.value = true
        _isError.value = false
        val item = ApiConfig().getApiService(BuildConfig.ARTICLE_API).getArticles()
        item.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(call: Call<ArticleResponse>,
                                    response: Response<ArticleResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                 _listArticle.value = response.body()?.results as List<ResultsItem>
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
}