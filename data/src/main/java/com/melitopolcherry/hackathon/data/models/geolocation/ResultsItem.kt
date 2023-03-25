package com.melitopolcherry.hackathon.data.models.geolocation

import com.google.gson.annotations.SerializedName

data class ResultsItem(

    @field:SerializedName("formatted_address")
    val formattedAddress: String? = null,

    @field:SerializedName("geometry")
    val geometry: Geometry? = null,

    @field:SerializedName("place_id")
    val placeId: String? = null
)