package com.melitopolcherry.hackathon.adapters.search_section

interface ItemSearchCallback {

    val itemSearchCallback: Callback

    interface Callback {
        fun onClick(item: Any)
    }
}