package com.melitopolcherry.hackathon.adapters.callbacks

interface AutocompleteCallback {

    val itemAutocompleteCallback: Callback
    interface Callback {
        fun onClick(item: String)
    }
}