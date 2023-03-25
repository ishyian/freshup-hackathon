package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.Phone

interface ChangePhoneCallback {

    val changePhoneCallback: Callback

    interface Callback {
        fun onReturned(phone: Phone?)
    }

}