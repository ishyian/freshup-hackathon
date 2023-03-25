package com.melitopolcherry.hackathon.data.models.event.full

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Venue(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("ticket_evolution_id")
    val ticketEvolutionId: String? = null,

    @field:SerializedName("city")
    var city: City? = null,

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("building_name")
    val buildingName: String? = null,

    @field:SerializedName("region")
    val region: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
) : Parcelable {

    fun getFullPlace(): String {
        return "$name, ${city?.name}, ${city?.buildingName}, ${city?.region}"
    }

    fun getPerformerVenuePlace(): String {
        return "$buildingName, $name, $region"
    }

    fun getShortPlace(): String {
        return "${name}, ${city?.name}, ${city?.region}"
    }
}