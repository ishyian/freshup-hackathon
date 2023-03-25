package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.responces.PromoCode

interface AddPromocodeCallback {

    val addPromocodeCallback: Callback

    interface Callback {
        fun onAdded(promocode: PromoCode)
    }

}