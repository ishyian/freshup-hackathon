package com.melitopolcherry.hackathon.data.model


import com.google.gson.annotations.SerializedName

data class Place(
    @SerializedName("address")
    val address: String,
    @SerializedName("contacts")
    val contacts: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("schedule")
    val schedule: String,
    @SerializedName("type")
    val type: String
): Marker