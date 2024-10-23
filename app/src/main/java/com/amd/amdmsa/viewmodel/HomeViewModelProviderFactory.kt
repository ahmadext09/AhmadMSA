package com.amd.amdmsa.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amd.amdmsa.repository.HomeRepository
import com.amd.amdmsa.utility.AppUtility
import javax.inject.Inject



class HomeViewModelProviderFactory @Inject constructor(
    private val application: Application,
    private val homeRepository: HomeRepository,
    private val appUtility: AppUtility
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(application, homeRepository, appUtility) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}