package com.melitopolcherry.hackathon.data.models.suggested

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.event.full.Venue

data class SuggestedItem(

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