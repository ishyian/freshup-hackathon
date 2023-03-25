package com.melitopolcherry.hackathon.data.models.event.ocurrences

import com.google.gson.annotations.SerializedName

data class EventOccurrencesResponse(

    @field:SerializedName("occurrences")
    val occurrences: List<OccurrencesItem>? = null
)