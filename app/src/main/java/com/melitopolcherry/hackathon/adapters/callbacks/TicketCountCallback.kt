package com.melitopolcherry.hackathon.adapters.callbacks

interface TicketCountCallback {

    val ticketsCountCallback: Callback

    interface Callback {
        fun onClick(count: Int)
    }

}