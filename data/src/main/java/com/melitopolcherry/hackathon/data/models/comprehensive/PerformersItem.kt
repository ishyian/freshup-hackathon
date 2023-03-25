package com.melitopolcherry.hackathon.data.models.comprehensive

import android.os.Parcelable
import com.melitopolcherry.hackathon.data.models.event.full.Venue
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PerformersItem(

    @field:SerializedName("img_url", alternate = ["image_url"])
    val imageUrl: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("venue")
    val venue: Venue? = null,

    val subType: Int? = null

) : Parcelable