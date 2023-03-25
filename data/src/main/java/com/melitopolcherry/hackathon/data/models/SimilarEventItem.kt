package com.melitopolcherry.hackathon.data.models

import com.melitopolcherry.hackathon.data.models.event.full.Venue
import com.google.gson.annotations.SerializedName

data class SimilarEventItem(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("venue")
    val venue: Venue? = null
)