package com.melitopolcherry.hackathon.domain.managers.geoInfoProvider

import com.melitopolcherry.hackathon.data.models.geocities.GeoCitiesResponse
import com.melitopolcherry.hackathon.data.models.geolocation.LocationByCityResponse
import com.melitopolcherry.hackathon.data.models.geoname.GeonameResponse
import com.melitopolcherry.hackathon.data.models.weather.current.CurrentWeatherResponse
import com.melitopolcherry.hackathon.data.models.weather.hourly.HourlyWeatherResponse
import com.melitopolcherry.hackathon.data.repo.EvenzRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeoInfoManager @Inject constructor(private var repository: EvenzRepository) : IGeoInfoManager {

    override fun getNearCities(
        north: Double,
        south: Double,
        east: Double,
        west: Double,
        lang: String,
        username: String
    ): Single<GeonameResponse> {
        return repository.getNearCities(north, south, east, west, lang, username)
    }

    override fun searchCities(input: String): Single<GeoCitiesResponse> {
        return repository.searchCities(input)
    }

    override fun getCityLocation(placeID: String): Single<LocationByCityResponse> {
        return repository.getCityLocation(placeID)
    }

    override fun getCurrentWeather(lat: String, lon: String): Single<CurrentWeatherResponse> {
        return repository.getCurrentWeather(lat, lon)
    }

    override fun getHourlyWeather(lat: String, lon: String): Single<HourlyWeatherResponse> {
        return repository.getHourlyWeather(lat, lon)
    }
}