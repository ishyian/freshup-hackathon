package com.melitopolcherry.hackathon.adapters.callbacks

interface RefreshCallback {

    val refreshCallback: Callback

    interface Callback {
        fun needToRefresh()
    }
}