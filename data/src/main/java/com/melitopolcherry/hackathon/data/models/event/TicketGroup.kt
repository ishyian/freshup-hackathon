package com.melitopolcherry.hackathon.data.models.event

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TicketGroup(

    @field:SerializedName("available_quantity")
    val availableQuantity: Int? = null,

    @field:SerializedName("splits")
    val splits: List<Int>? = null,

    @field:SerializedName("section_name")
    val section: String? = null,

    @field:SerializedName("format")
    val format: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("row")
    val row: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("retail_price")
    val retailPrice: Double? = null,

    @field:SerializedName("public_notes")
    val publicNotes: String? = null,

    var url: String? = null

) : Parcelable