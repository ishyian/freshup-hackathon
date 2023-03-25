package com.melitopolcherry.hackathon.data.models.event.full

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.ProvidedIds
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventFull(

    @field:SerializedName("venue")
    val venue: Venue? = null,

    @field:SerializedName("performers")
    val performers: List<PerformersItem>? = null,

//    @field:SerializedName("like_count")
//    val likeCount: Int? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("event_description")
    val eventDescription: String? = null,

//    @field:SerializedName("attribution_licence_link_text")
//    val attributionLicenceLinkText: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("event_category")
    val eventCategory: String? = null,

//    @field:SerializedName("track_count")
//    val trackCount: Int? = null,

    @field:SerializedName("video_url")
    val videoUrl: String? = null,

//    @field:SerializedName("attribution_source_link")
//    val attributionSourceLink: String? = null,
//
//    @field:SerializedName("attribution_licence_link")
//    val attributionLicenceLink: String? = null,

    @field:SerializedName("is_tracked")
    var isTracked: Boolean? = null,

    @field:SerializedName("id")
    val id: String? = null,

//    @field:SerializedName("attribution_source_link_text")
//    val attributionSourceLinkText: String? = null,

    @field:SerializedName("start_date")
    var startDate: String? = null,

//    @field:SerializedName("is_liked")
//    var isLiked: Boolean? = null,

    @field:SerializedName("provided_ids")
    val providedIds: ProvidedIds? = null
) : Parcelable