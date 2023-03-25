package com.melitopolcherry.hackathon.data.models.tracking

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.data.models.comprehensive.VenuesItem

data class TrackedEntetiesResponse(

    @field:SerializedName("performers")
    val performers: List<PerformersItem>? = null,

    @field:SerializedName("venues")
    val venues: List<VenuesItem>? = null,

    @field:SerializedName("events")
    val events: List<EventsItem>? = null
)