package com.melitopolcherry.hackathon.domain.managers

import com.melitopolcherry.hackathon.data.repo.EvenzRepository
import com.melitopolcherry.hackathon.domain.managers.account.AccountManager
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.EventManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import com.melitopolcherry.hackathon.domain.managers.geoInfoProvider.GeoInfoManager
import com.melitopolcherry.hackathon.domain.managers.geoInfoProvider.IGeoInfoManager
import com.melitopolcherry.hackathon.domain.managers.registration.IRegistrationManager
import com.melitopolcherry.hackathon.domain.managers.registration.RegistrationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ManagersModule {

    @Provides
    @Singleton
    fun provideRegistrationManager(repository: EvenzRepository): IRegistrationManager {
        return RegistrationManager(repository)
    }

    @Provides
    @Singleton
    fun provideEventManager(
        repository: EvenzRepository
    ): IEventManager {
        return EventManager(repository)
    }

    @Provides
    @Singleton
    fun provideAccountManager(
        repository: EvenzRepository
    ): IAccountManager {
        return AccountManager(repository)
    }

    @Provides
    @Singleton
    fun provideGeoInfoManager(
        repository: EvenzRepository
    ): IGeoInfoManager {
        return GeoInfoManager(repository)
    }
}