package com.geby.stuntshield.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnalyzeResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: Status? = null
) : Parcelable

@Parcelize
data class Stunting(

	@field:SerializedName("presentase")
	val presentase: String? = null,

	@field:SerializedName("class")
	val jsonMemberClass: String? = null
) : Parcelable

@Parcelize
data class Status(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
data class Weight(

	@field:SerializedName("presentase")
	val presentase: String? = null,

	@field:SerializedName("class")
	val jsonMemberClass: String? = null
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("ideal")
	val ideal: Ideal? = null,

	@field:SerializedName("recommendation")
	val recommendation: String? = null,

	@field:SerializedName("weight")
	val weight: Weight? = null,

	@field:SerializedName("stunting")
	val stunting: Stunting? = null
) : Parcelable


@Parcelize
data class Ideal(

	@field:SerializedName("presentase")
	val presentase: String? = null,

	@field:SerializedName("class")
	val jsonMemberClass: String? = null
) : Parcelable

