package com.melitopolcherry.hackathon.data.restApi.ahal

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RestApiBuilderHelper {
    private var retrofit: Retrofit? = null
    private val interceptors: MutableList<Interceptor> = ArrayList()
    private val gsonTypeAdapters: MutableMap<Class<*>, Any> = HashMap()

    private val logger = HttpLoggingInterceptor.Logger {
        HttpLoggingInterceptor.Logger { message -> Log.i("evnApp", message) }
    }

    fun addInterceptor(interceptor: Interceptor): RestApiBuilderHelper {
        interceptors.add(interceptor)
        return this
    }

    fun addLoggingInterceptor(level: HttpLoggingInterceptor.Level): RestApiBuilderHelper {
        addInterceptor(HttpLoggingInterceptor(logger).setLevel(level))
        return this
    }

    fun <T> createRetrofitApi(url: String, service: Class<T>): T {
        if (retrofit == null) {
            retrofit = Retrofit.Builder().baseUrl(url)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(provideGsonConverterFactory()).client(createOkHttpClient())
                .build()
        }

        return retrofit?.create(service)!!
    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)

        for (interceptor in interceptors) {
            builder.addInterceptor(interceptor)
        }

        return builder.build()
    }

    private fun provideGsonConverterFactory(): GsonConverterFactory {
        val gsonBuilder = GsonBuilder()

        for ((key, value) in gsonTypeAdapters) {
            gsonBuilder.registerTypeAdapter(key, value)
        }

        return GsonConverterFactory.create(gsonBuilder.create())
    }
}
