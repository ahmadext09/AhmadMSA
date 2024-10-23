package com.amd.amdmsa.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.amd.amdmsa.R
import com.amd.amdmsa.model.FourSquareNearbyResponse
import com.amd.amdmsa.model.FsqNearbyQueryParam
import com.amd.amdmsa.repository.HomeRepository
import com.amd.amdmsa.utility.AppConstants
import com.amd.amdmsa.utility.AppUtility
import com.amd.amdmsa.utility.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    val app: Application,
    private val repository: HomeRepository,
    private val appUtility: AppUtility
) : AndroidViewModel(app) {


    val nearByPizzaLocationLiveData: MutableLiveData<Resource<FourSquareNearbyResponse>> = MutableLiveData()
    val nearByJuiceLocationLiveData: MutableLiveData<Resource<FourSquareNearbyResponse>> = MutableLiveData()


    suspend fun getNearByLocations(queryParam: FsqNearbyQueryParam, nearByLocationsLiveData: MutableLiveData<Resource<FourSquareNearbyResponse>>) {

        try {
            nearByLocationsLiveData.postValue(Resource.Loading())
            if (appUtility.hasInternetConnection()) {
                val response = repository.getNearbyLocations(queryParam)
                val handledResponse = handleHomepageContentResponse(response)
                nearByLocationsLiveData.postValue(handledResponse)


            } else {
                nearByLocationsLiveData.postValue(Resource.Error(app.getString(R.string.internet_connection), AppConstants.ERROR_CODES.APP_ERROR_CODE))
            }
        } catch (e: Exception) {
            val errorMsg = handleException(e)
            nearByLocationsLiveData.postValue(Resource.Error(errorMsg, AppConstants.ERROR_CODES.APP_ERROR_CODE))
        }
    }

    private fun handleHomepageContentResponse(response: Response<FourSquareNearbyResponse>): Resource<FourSquareNearbyResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse, response.code())
            }
        }
        val errorMsg = if (TextUtils.isEmpty(response.message())) {
            app.getString(R.string.something_went_wrong)
        } else {
            response.message()
        }
        return Resource.Error(errorMsg, response.code())
    }


    private fun handleException(e: Exception): String {
        val errorMsg = when (e) {
            is IOException -> app.getString(R.string.network_failure)
            is ConnectException -> app.getString(R.string.internet_connection)
            else -> app.getString(R.string.something_went_wrong)
        }
        return errorMsg
    }

}