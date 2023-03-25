package com.melitopolcherry.hackathon.data.models.geolocation

import com.google.gson.annotations.SerializedName

data class Geometry(

    @field:SerializedName("location")
    val location: Location? = null
)