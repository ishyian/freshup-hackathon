package com.melitopolcherry.hackathon.data.models.geolocation

import com.google.gson.annotations.SerializedName

data class LocationByCityResponse(

    @field:SerializedName("results")
    val results: List<ResultsItem>? = null,

    @field:SerializedName("status")
    val status: String? = null
)