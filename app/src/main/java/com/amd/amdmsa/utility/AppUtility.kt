package com.amd.amdmsa.utility


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.Window
import com.amd.amdmsa.R
import javax.inject.Inject


class AppUtility @Inject constructor(
    private val context: Context
) {
    private lateinit var mProgressDialog: Dialog

    fun hasInternetConnection(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


    fun showProgressDialog(activity: Activity) {
        try {
            getWalletStyleProgressDialog(activity)?.let {
                mProgressDialog = it
                if (::mProgressDialog.isInitialized && !mProgressDialog.isShowing) {
                    mProgressDialog.show()
                }
            }
        } catch (e: Exception) {
        }
    }

    fun hideProgressDialog() {
        try {
            if (::mProgressDialog.isInitialized && mProgressDialog.isShowing) {
                mProgressDialog.dismiss()
            }
        } catch (e: Exception) {
        }
    }

    private fun getWalletStyleProgressDialog(activity: Activity?): Dialog? {
        try {
            if (activity != null) {
                val progressDialog = Dialog(activity)
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                progressDialog.setCancelable(false)
                progressDialog.setContentView(R.layout.lyt_progress_bar)
                progressDialog.window!!.setBackgroundDrawableResource(R.color.transparent)
                return progressDialog
            }
        } catch (e: Exception) {
        }
        return null
    }
}

