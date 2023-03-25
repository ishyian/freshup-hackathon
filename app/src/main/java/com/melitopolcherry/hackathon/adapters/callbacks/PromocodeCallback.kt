package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.responces.PromoCode

interface PromocodeCallback {

    val promocodeCallback: Callback

    interface Callback {
        fun onSelected(promocode: PromoCode?)
    }

}