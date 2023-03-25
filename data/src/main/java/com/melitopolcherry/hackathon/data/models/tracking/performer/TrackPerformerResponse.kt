package com.melitopolcherry.hackathon.data.models.tracking.performer

import com.google.gson.annotations.SerializedName

data class TrackPerformerResponse(

    @field:SerializedName("trackedCount")
    val trackedCount: Int? = null,

    @field:SerializedName("performerId")
    val performerId: String? = null
)