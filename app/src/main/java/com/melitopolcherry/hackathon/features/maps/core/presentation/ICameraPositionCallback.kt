package com.melitopolcherry.hackathon.features.maps.core.presentation

import com.google.android.gms.maps.model.LatLng

interface ICameraPositionCallback {

    val positionChangeCallback: Callback

    interface Callback {
        fun onChange(position: LatLng)
        fun showButton()
    }
}