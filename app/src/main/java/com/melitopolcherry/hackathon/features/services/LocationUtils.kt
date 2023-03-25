package com.melitopolcherry.hackathon.features.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng

open class LocationUtils(val context: Context, val callback: LocationCallback) {

    private var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    fun initLocation() {
        val lastGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val lastNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val lastPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        when {
            lastGPS != null -> {
                callback.onLocationChanged(LatLng(lastGPS.latitude, lastGPS.longitude))
            }
            lastNetwork != null -> {
                callback.onLocationChanged(LatLng(lastNetwork.latitude, lastNetwork.longitude))
            }
            lastPassive != null -> {
                callback.onLocationChanged(LatLng(lastPassive.latitude, lastPassive.longitude))
            }
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0L, 0f,
                locationListener
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0L, 0f,
                locationListener
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            callback.onLocationChanged(LatLng(location.latitude, location.longitude))
        }

        override fun onProviderEnabled(provider: String) {
            initLocation()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderDisabled(provider: String) {
            callback.providerDisabled(true)
        }
    }

    @SuppressLint("MissingPermission")
    fun removeLocationListener() {
        locationManager.removeUpdates(locationListener)
    }

    interface LocationCallback {
        fun onLocationChanged(location: LatLng)
        fun providerDisabled(boolean: Boolean)
    }
}