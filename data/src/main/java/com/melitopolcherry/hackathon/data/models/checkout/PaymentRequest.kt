package com.melitopolcherry.hackathon.data.models.checkout

data class PaymentRequest(
    val application_uri: String,
    val lock_id: String?,
    val payment_method_source: String,
    val quantity: String,
    val ticket_group_id: String,
    val ticket_source: String,
    val payment_method_id: String?,
    var promocode_id: String? = null,
    var insurance_quote: String? = null,
)