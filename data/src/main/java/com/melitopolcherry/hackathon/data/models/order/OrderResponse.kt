package com.melitopolcherry.hackathon.data.models.order

import com.google.gson.annotations.SerializedName

data class OrderResponse(

    @field:SerializedName("requires_action")
    val requiresAction: Boolean? = null,

    @field:SerializedName("redirect_url")
    val redirectUrl: String? = null,

    @field:SerializedName("order_intent_id")
    val orderIntentId: String? = null,

    @field:SerializedName("payment_client_secret")
    val paymentClientSecret: String? = null
)