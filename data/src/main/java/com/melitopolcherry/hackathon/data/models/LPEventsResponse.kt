package com.melitopolcherry.hackathon.data.models

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem

data class LPEventsResponse(
    @field:SerializedName("events")
    val events: List<EventsItem>? = null
)