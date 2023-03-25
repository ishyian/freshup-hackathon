package com.melitopolcherry.hackathon.data.models.order

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.event.full.Venue
import java.util.*

data class OrderItem(

    @field:SerializedName("venue")
    val venue: Venue? = null,

    @field:SerializedName("total")
    val total: String? = null,

    @field:SerializedName("quantity")
    val quantity: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("time")
    val time: String? = null,

    @field:SerializedName("tickets")
    val tickets: ArrayList<String>? = null,

    @field:SerializedName("ticket_urls")
    val ticketUrls: List<String>? = null
)