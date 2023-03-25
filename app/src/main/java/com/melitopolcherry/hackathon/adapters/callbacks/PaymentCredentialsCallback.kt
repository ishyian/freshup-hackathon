package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.checkout.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams

interface PaymentCredentialsCallback {

    val paymentCredentialsCallback: Callback

    interface Callback {
        fun onCardReturned(card: PaymentMethod, paymentMethodCreateParams: PaymentMethodCreateParams?)
    }

}