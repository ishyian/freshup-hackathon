package com.melitopolcherry.hackathon.data.models.performer

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem

data class PerformerDetails(
    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("attribution_licence_link_text")
    val attributionLicenceLinkText: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("full_description")
    val fullDescription: String? = null,

    @field:SerializedName("video_url")
    val videoUrl: String? = null,

    @field:SerializedName("attribution_source_link")
    val attributionSourceLink: String? = null,

    @field:SerializedName("attribution_licence_link")
    val attributionLicenceLink: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("is_tracked")
    var isTracked: Boolean? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("attribution_source_link_text")
    val attributionSourceLinkText: String? = null,

    @field:SerializedName("events")
    val events: List<EventsItem>? = null
)