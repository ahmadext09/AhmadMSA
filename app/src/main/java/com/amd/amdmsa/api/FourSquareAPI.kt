package com.amd.amdmsa.api

import com.amd.amdmsa.BuildConfig
import com.amd.amdmsa.model.FourSquareNearbyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FourSquareAPI {

    @GET("v3/places/nearby")
    suspend fun getNearbyLocations(
        @Header("Authorization") authorization: String = BuildConfig.FOUR_SQUARE_API_KEY,
        @Query("ll") ll: String,
        @Query("radius") radius: Int,
        @Query("query") query: String,
        @Query("limit") limit: Int,
        @Query("fields") fields: String
    ): Response<FourSquareNearbyResponse>

}