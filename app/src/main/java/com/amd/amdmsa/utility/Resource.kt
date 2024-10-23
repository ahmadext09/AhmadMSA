package com.amd.amdmsa.utility

sealed class Resource<T>(
    val data: T? = null,
    val code: Int? = null,
    val message: String? = null,
) {
    class Success<T>(data: T, code: Int) : Resource<T>(data, code)
    class Error<T>(message: String, errorCode: Int, data: T? = null) : Resource<T>(data, errorCode, message)
    class Loading<T> : Resource<T>()
}
