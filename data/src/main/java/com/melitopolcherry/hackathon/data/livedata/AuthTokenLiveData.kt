package com.melitopolcherry.hackathon.data.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.data.models.login.AccessToken

class AuthTokenLiveData : LiveData<AccessToken>() {

    private var liveData = MutableLiveData<AccessToken>()

    fun setToken(token: AccessToken) {
        postValue(token)
    }

    fun clearToken() {
        value = null
    }

    companion object {
        @JvmStatic
        val instance = AuthTokenLiveData()
    }
}