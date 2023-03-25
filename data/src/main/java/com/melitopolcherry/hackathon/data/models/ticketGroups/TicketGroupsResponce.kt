package com.melitopolcherry.hackathon.data.models.ticketGroups

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.event.TicketGroup

data class TicketGroupsResponce(

    @field:SerializedName("ticket_groups")
    val ticketGroups: List<TicketGroup>? = null,

    @field:SerializedName("total_entries")
    val totalEntries: Int? = null
)