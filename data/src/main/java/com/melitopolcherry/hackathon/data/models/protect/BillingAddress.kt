package com.melitopolcherry.hackathon.data.models.protect

import com.google.gson.annotations.SerializedName

data class BillingAddress(

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("address_line_1")
    val addressLine1: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("postal_code")
    val postalCode: String? = null
)
