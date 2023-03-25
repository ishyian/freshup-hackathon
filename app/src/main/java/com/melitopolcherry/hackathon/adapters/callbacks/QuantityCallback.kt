package com.melitopolcherry.hackathon.adapters.callbacks

interface QuantityCallback {

    val quantityCallback: Callback

    interface Callback {
        fun onClick(quantities: ArrayList<Int>)
    }

}