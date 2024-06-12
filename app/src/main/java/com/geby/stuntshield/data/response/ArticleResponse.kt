package com.geby.stuntshield.data.response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null
)

data class ResultsItem(

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("title")
	val title: String? = null
)
