package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.Address

interface DeliveryInfoCallback {

    val deliveryInfoCallback: Callback

    interface Callback {
        fun onReturned(address: Address?)
    }

}