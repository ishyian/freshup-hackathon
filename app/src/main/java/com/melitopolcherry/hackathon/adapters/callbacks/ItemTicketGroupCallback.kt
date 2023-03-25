package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.databinding.ItemTicketGroupBinding

interface ItemTicketGroupCallback {

    val ticketsClickCallback: Callback

    interface Callback {
        fun onClick(ticket: TicketGroup?, view: ItemTicketGroupBinding)
    }
}