package com.melitopolcherry.hackathon.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TicketGroupsRequest(

    @field:SerializedName("provided_ids")
    val providedIds: ProvidedIds? = null
)

@Parcelize
data class ProvidedIds(
    @field:SerializedName("TICKET_EVOLUTION")
    val ticketevolution: String? = null,

    @field:SerializedName("TICKET_NETWORK")
    val ticketnetwork: String? = null
) : Parcelable
