package com.melitopolcherry.hackathon.data.models.event.ocurrences

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.ProvidedIds
import com.melitopolcherry.hackathon.data.models.event.full.City

data class OccurrencesItem(

    @field:SerializedName("city")
    val city: City? = null,

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("venue_id")
    val venueId: String? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null,

    @field:SerializedName("provided_ids")
    val providedIds: ProvidedIds? = null
)