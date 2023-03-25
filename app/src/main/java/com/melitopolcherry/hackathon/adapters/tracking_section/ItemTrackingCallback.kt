package com.melitopolcherry.hackathon.adapters.tracking_section

interface ItemTrackingCallback {

    val itemTrackingCallback: Callback

    interface Callback {
        fun onItemClick(item: Any)
        fun showAllEvents(type: String)
    }
}