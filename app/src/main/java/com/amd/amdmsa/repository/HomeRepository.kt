package com.amd.amdmsa.repository

import com.amd.amdmsa.api.FourSquareAPI
import com.amd.amdmsa.model.FsqNearbyQueryParam
import javax.inject.Inject


class HomeRepository @Inject constructor(
    private val api: FourSquareAPI
) {

    suspend fun getNearbyLocations(query: FsqNearbyQueryParam) =
        api.getNearbyLocations(ll = query.ll, radius = query.radius, query = query.query, limit = query.limit, fields = query.fields)
}