package com.melitopolcherry.hackathon.data.model

import com.google.gson.annotations.SerializedName

data class Place(
    @SerializedName("address")
    val address: String,
    @SerializedName("facultyId")
    val facultyId: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String
) : Marker