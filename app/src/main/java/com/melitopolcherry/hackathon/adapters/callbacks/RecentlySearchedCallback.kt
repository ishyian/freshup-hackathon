package com.melitopolcherry.hackathon.adapters.callbacks

interface RecentlySearchedCallback {

    val recentlySearchedCallback: Callback
    interface Callback {
        fun onClick(item: String)
    }
}