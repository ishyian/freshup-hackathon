package com.melitopolcherry.hackathon.data.models.comprehensive

import com.google.gson.annotations.SerializedName

data class ComprehensiveResponse(

    @field:SerializedName("performers")
    val performers: List<PerformersItem>? = null,

    @field:SerializedName("totalSize")
    val totalSize: Int? = null,

    @field:SerializedName("top")
    val top: List<TopItem>? = null,

    @field:SerializedName("venues")
    val venues: List<VenuesItem>? = null,

    @field:SerializedName("events")
    val events: List<EventsItem>? = null
)