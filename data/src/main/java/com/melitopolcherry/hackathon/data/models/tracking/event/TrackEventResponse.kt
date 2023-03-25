package com.melitopolcherry.hackathon.data.models.tracking.event

import com.google.gson.annotations.SerializedName

data class TrackEventResponse(

    @field:SerializedName("event_id")
    val eventId: String? = null,

    @field:SerializedName("tracked_count")
    val trackedCount: Int? = null
)