package com.melitopolcherry.hackathon.data.models.event.small

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.ProvidedIds
import com.melitopolcherry.hackathon.data.models.event.full.Venue

data class EventMap(

    @field:SerializedName("provided_ids")
    val providedIds: ProvidedIds? = null,

    @field:SerializedName("venue")
    val venue: Venue? = null,

    @field:SerializedName("like_count")
    var likeCount: Int? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("event_description")
    val eventDescription: String? = null,

    @field:SerializedName("is_tracked")
    var isTracked: Boolean? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("event_category")
    val eventCategory: String? = null,

    @field:SerializedName("track_count")
    var trackCount: Int? = null,

    @field:SerializedName("start_date")
    var startDate: String? = null,

    @field:SerializedName("is_liked")
    var isLiked: Boolean? = null
)