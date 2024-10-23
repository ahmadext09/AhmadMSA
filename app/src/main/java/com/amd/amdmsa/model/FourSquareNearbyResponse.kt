package com.amd.amdmsa.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FourSquareNearbyResponse(
    @SerializedName("results")
    val results: ArrayList<Place>
)

@Keep
data class Place(
    @SerializedName("fsq_id")
    val fsqId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("distance")
    val distance: Int,

    @SerializedName("location")
    val location: Location

)


@Keep
data class Location(
    @SerializedName("address")
    val address: String,

    @SerializedName("address_extended")
    val addressExtended: String?,

    @SerializedName("census_block")
    val censusBlock: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("cross_street")
    val crossStreet: String?,

    @SerializedName("dma")
    val dma: String,

    @SerializedName("formatted_address")
    val formattedAddress: String,

    @SerializedName("locality")
    val locality: String,

    @SerializedName("postcode")
    val postcode: String,

    @SerializedName("region")
    val region: String
)
