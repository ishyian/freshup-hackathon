package com.melitopolcherry.hackathon.data.models.responces

import com.google.gson.annotations.SerializedName

data class SuggestedShipmentResponse(
    @field:SerializedName("price")
    val price: String? = null
)