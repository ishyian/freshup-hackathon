package com.melitopolcherry.hackathon.data.models

import com.google.gson.annotations.SerializedName

data class FeesResponse(

    @field:SerializedName("percentage")
    val percentage: Boolean? = null,

    @field:SerializedName("service_fee")
    val serviceFee: String? = null
)
