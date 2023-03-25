package com.melitopolcherry.hackathon.data.models.venue

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.tracking.venue.City

data class VenueDetails(

    @field:SerializedName("city")
    val city: City? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("is_tracked")
    var isTracked: Boolean? = null,

    @field:SerializedName("is_liked")
    var liked: Boolean? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("configuration_id")
    val configurationId: Int? = null,

    @field:SerializedName("events")
    val events: List<EventsItem>? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
)