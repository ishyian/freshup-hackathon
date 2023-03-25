package com.melitopolcherry.hackathon.data.models.geocities

import com.google.gson.annotations.SerializedName

data class GeoCitiesResponse(

    @field:SerializedName("predictions")
    val predictions: List<PredictionsItem>? = null
)