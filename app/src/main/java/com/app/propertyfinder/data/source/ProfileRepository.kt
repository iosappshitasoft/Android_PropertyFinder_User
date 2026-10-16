package com.app.propertyfinder.data.source

import com.app.propertyfinder.api.ApiInterface
import com.app.propertyfinder.base.BaseApiResponse
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiInterface: ApiInterface): BaseApiResponse() {

    companion object {
        val TAG = ProfileRepository::class.java.simpleName
    }
}