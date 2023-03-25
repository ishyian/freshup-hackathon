package com.melitopolcherry.hackathon.data.models.event.full

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("building_name")
    val buildingName: String? = null,

//    @field:SerializedName("country_short_name")
//    val countryShortName: String? = null,
//
//    @field:SerializedName("post_code")
//    val postCode: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("region")
    val region: String? = null
) : Parcelable