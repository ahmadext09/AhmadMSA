package com.amd.amdmsa.model

import androidx.annotation.Keep
import com.amd.amdmsa.utility.AppConstants
import com.google.gson.annotations.SerializedName


@Keep
data class FsqNearbyQueryParam(
    @SerializedName("ll")
    val ll: String,

    @SerializedName("radius")
    val radius: Int,

    @SerializedName("query")
    val query: String,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("fields")
    val fields: String = AppConstants.FOUR_SQUARE_QUERY_FIELDS

)