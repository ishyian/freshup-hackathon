package com.melitopolcherry.hackathon.data.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.melitopolcherry.hackathon.data.restApi.EvenzApi
import com.melitopolcherry.hackathon.data.restApi.EvenzApiFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Singleton
    @Provides
    fun providesEvenzApi(
        chuckerInterceptor: ChuckerInterceptor
    ): EvenzApi {
        return EvenzApiFactory.createApi(chuckerInterceptor)
    }

    @Singleton
    @Provides
    fun provideChucker(
        @ApplicationContext context: Context
    ): ChuckerInterceptor {
        return ChuckerInterceptor
            .Builder(context)
            .build()
    }
}