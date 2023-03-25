package com.melitopolcherry.hackathon.data.models.comprehensive

import com.melitopolcherry.hackathon.data.models.event.full.Venue
import com.google.gson.annotations.SerializedName

data class TopItem(

    @field:SerializedName("ticket_evolution_id")
    val ticketEvolutionId: String? = null,

    @field:SerializedName("img_url", alternate = ["image_url"])
    val imageUrl: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("place")
    val place: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null,

    @field:SerializedName("venue")
    val venue: Venue? = null
)