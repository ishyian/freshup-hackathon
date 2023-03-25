package com.melitopolcherry.hackathon.data.models.tracking.performer

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem

data class TrackingPerformersList(

    @field:SerializedName("totalItems")
    val totalItems: Int? = null,

    @field:SerializedName("size")
    val size: Int? = null,

    @field:SerializedName("totalPages")
    val totalPages: Int? = null,

    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("items")
    val items: List<PerformersItem>? = null
)
