package com.melitopolcherry.hackathon.data.models.tracking.venue

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.comprehensive.VenuesItem

data class TrackingVenuesList(

    @field:SerializedName("totalItems")
    val totalItems: Int? = null,

    @field:SerializedName("size")
    val size: Int? = null,

    @field:SerializedName("totalPages")
    val totalPages: Int? = null,

    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("items")
    val items: List<VenuesItem>? = null
)