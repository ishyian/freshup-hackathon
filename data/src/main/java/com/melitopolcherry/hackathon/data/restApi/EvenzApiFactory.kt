package com.melitopolcherry.hackathon.data.restApi

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.melitopolcherry.hackathon.data.BuildConfig
import com.melitopolcherry.hackathon.data.restApi.ahal.RestApiBuilderHelper
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Singleton
class EvenzApiFactory {

    companion object {
        @JvmStatic
        fun createApi(chucker: ChuckerInterceptor): EvenzApi {
            val apiBuilderHelper = RestApiBuilderHelper()

            if (BuildConfig.DEBUG) {
                apiBuilderHelper.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY)
            }

            apiBuilderHelper.addInterceptor(chucker)
            return apiBuilderHelper.createRetrofitApi(
                BuildConfig.SERVER_URL,
                EvenzApi::class.java
            )
        }
    }
}