package com.melitopolcherry.hackathon.data.models.tracking.venue

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(

    @field:SerializedName("building_name")
    val buildingName: String? = null,

    @field:SerializedName("region")
    val region: String? = null,

    @field:SerializedName("name")
    val name: String? = null

) : Parcelable