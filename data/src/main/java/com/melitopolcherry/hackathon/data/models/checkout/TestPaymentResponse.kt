package com.melitopolcherry.hackathon.data.models.checkout

import com.google.gson.annotations.SerializedName

data class TestPaymentResponse(

    @field:SerializedName("requires_action")
    val actionRequired: Boolean? = null,

    @field:SerializedName("order_instant_id")
    var orderInstantId: String? = "evenz://return_to_app",

    @field:SerializedName("payment_client_secret")
    var paymentClientSecret: String? = "evenz://return_to_app"
)
