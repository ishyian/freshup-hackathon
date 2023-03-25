package com.melitopolcherry.hackathon.data.models.tracking.venue

import com.google.gson.annotations.SerializedName

data class TrackVenueResponse(

    @field:SerializedName("venueId")
    val venueId: String? = null,

    @field:SerializedName("trackedCount")
    val trackedCount: Int? = null
)