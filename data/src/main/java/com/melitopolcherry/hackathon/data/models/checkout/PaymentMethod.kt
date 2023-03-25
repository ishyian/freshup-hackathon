package com.melitopolcherry.hackathon.data.models.checkout

import com.melitopolcherry.hackathon.data.models.protect.BillingAddress
import com.google.gson.annotations.SerializedName

data class PaymentMethod(
    @field:SerializedName("card_exp_year")
    var cardExpYear: String? = null,
    @field:SerializedName("card_number")
    var cardNumber: String? = null,
    @field:SerializedName("cardholder_name")
    var cardholderName: String? = null,
    @field:SerializedName("application_uri")
    var applicationUri: String? = "evenz://return_to_app",
    @field:SerializedName("card_cvc")
    var cardCvc: String? = null,
    @field:SerializedName("card_exp_month")
    var cardExpMonth: String? = null,
    @field:SerializedName("billing_address")
    var billingAddress: BillingAddress? = null
) {
    fun isCardFilled(): Boolean {
        return cardExpYear != null &&
            cardNumber != null &&
            cardCvc != null &&
            cardExpMonth != null &&
            cardholderName != null &&
            billingAddress != null
    }
}
