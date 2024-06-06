package com.geby.stuntshield.data.response

import com.google.gson.annotations.SerializedName

data class AnalyzeResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: Status? = null
)

data class Data(

	@field:SerializedName("presentase")
	val presentase: String? = null,

	@field:SerializedName("class")
	val jsonMemberClass: String? = null
)

data class Status(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
)
