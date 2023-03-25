package com.melitopolcherry.hackathon.domain.managers.geoInfoProvider

import com.melitopolcherry.hackathon.data.models.geocities.GeoCitiesResponse
import com.melitopolcherry.hackathon.data.models.geolocation.LocationByCityResponse
import com.melitopolcherry.hackathon.data.models.geoname.GeonameResponse
import com.melitopolcherry.hackathon.data.models.weather.current.CurrentWeatherResponse
import com.melitopolcherry.hackathon.data.models.weather.hourly.HourlyWeatherResponse
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface IGeoInfoManager {

    fun getNearCities(
        north: Double,
        south: Double,
        east: Double,
        west: Double,
        lang: String,
        username: String
    ): Single<GeonameResponse>

    fun searchCities(input: String): Single<GeoCitiesResponse>
    fun getCityLocation(placeID: String): Single<LocationByCityResponse>

    fun getCurrentWeather(lat: String, lon: String): Single<CurrentWeatherResponse>
    fun getHourlyWeather(lat: String, lon: String): Single<HourlyWeatherResponse>

}