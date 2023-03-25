package com.melitopolcherry.hackathon.data.models

import com.google.gson.annotations.SerializedName

data class CheckPromocodeResponse(

    @field:SerializedName("end_date")
    val endDate: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("one_time_use")
    val oneTimeUse: Boolean? = null,

    @field:SerializedName("percentage")
    val percentage: Boolean? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("value")
    val value: Int? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null
)
