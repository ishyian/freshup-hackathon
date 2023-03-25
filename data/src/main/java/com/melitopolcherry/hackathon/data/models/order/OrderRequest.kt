package com.melitopolcherry.hackathon.data.models.order

import com.google.gson.annotations.SerializedName

data class OrderRequest(

    @field:SerializedName("quantity")
    var quantity: Int? = null,

    @field:SerializedName("promocode_id")
    var promocodeId: String? = null,

    @field:SerializedName("payment_method_source")
    var payment_method_source: String?,

    @field:SerializedName("ticket_group_id")
    var ticketGroupId: String? = null,

    @field:SerializedName("ticket_source")
    var ticketSource: String? = null,

    @field:SerializedName("application_uri")
    var applicationUri: String? = "evenz://return_to_app",

    @field:SerializedName("insurance_quote")
    var insuranceQuote: String? = null,

    @field:SerializedName("lock_id")
    var lockId: String? = null
)