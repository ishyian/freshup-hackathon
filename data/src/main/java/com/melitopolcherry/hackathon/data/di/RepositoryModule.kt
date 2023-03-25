package com.melitopolcherry.hackathon.data.di

import com.melitopolcherry.hackathon.data.repo.EvenzRepository
import com.melitopolcherry.hackathon.data.repo.EvenzRepositoryImpl
import com.melitopolcherry.hackathon.data.restApi.EvenzApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun providesEvenzRepository(
        evenzApi: EvenzApi
    ): EvenzRepository {
        return EvenzRepositoryImpl(evenzApi)
    }
}