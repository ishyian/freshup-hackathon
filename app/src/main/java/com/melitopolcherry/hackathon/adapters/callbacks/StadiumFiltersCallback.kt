package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.TicketsFilter
import com.melitopolcherry.hackathon.data.models.event.TicketGroup

interface StadiumFiltersCallback {

    val stadiumFiltersCallback: Callback

    interface Callback {
        fun onFilter(filteredList: List<TicketGroup>, filter: TicketsFilter)
    }

}