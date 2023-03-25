package com.melitopolcherry.hackathon.data.models.comprehensive

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.event.full.City
import kotlinx.parcelize.Parcelize

@Parcelize
data class VenuesItem(

    @field:SerializedName("img_url", alternate = ["image_url"])
    val imageUrl: String? = null,

    @field:SerializedName("ticket_evolution_id")
    val ticketEvolutionId: String? = null,

    @field:SerializedName("city")
    var city: City? = null,

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("place")
    val place: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
) : Parcelable {

    fun getShortPlace(): String {
        return "${name}, ${city?.name}, ${city?.region}"
    }
}