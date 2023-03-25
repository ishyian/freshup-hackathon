package com.melitopolcherry.hackathon.data.models.responces

import com.google.gson.annotations.SerializedName

data class PromoCode(

    @field:SerializedName("valid_until")
    val validUntil: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("single_use")
    val singleUse: Boolean? = null,

    @field:SerializedName("percentage")
    val percentage: Boolean? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("value")
    val value: String? = null
)