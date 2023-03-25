package com.melitopolcherry.hackathon.data.models.checkout

import com.google.gson.annotations.SerializedName

data class CreditCardResponse(

    @field:SerializedName("action_required")
    val actionRequired: Boolean? = null,

    @field:SerializedName("redirect_url")
    var redirectUrl: String? = "evenz://return_to_app"
)
