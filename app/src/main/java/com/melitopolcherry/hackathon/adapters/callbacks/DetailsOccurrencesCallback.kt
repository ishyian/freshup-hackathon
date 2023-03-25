package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.event.ocurrences.OccurrencesItem


interface DetailsOccurrencesCallback {

    val occurrencesCallback: Callback

    interface Callback {
        fun onClick(occurrencesItem: OccurrencesItem)
    }
}