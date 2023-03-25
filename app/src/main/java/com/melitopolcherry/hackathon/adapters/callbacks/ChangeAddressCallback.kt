package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.Address

interface ChangeAddressCallback {

    val changeAddressCallback: Callback

    interface Callback {
        fun onChanged(address: Address)
    }

}