package com.melitopolcherry.hackathon.adapters.callbacks

interface LoadNextPageCallback {

    val loadNextPageCallback: Callback

    interface Callback {
        fun needToLoadNext(page: Int)
    }
}