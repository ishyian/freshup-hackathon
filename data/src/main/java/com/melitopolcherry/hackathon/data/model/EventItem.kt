package com.melitopolcherry.hackathon.data.model

import com.google.gson.annotations.SerializedName

data class EventItem(
    @SerializedName("classroom")
    val classroom: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("facultyId")
    val facultyId: String,
    @SerializedName("group")
    val group: String,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("subjectName")
    val subjectName: String,
    @SerializedName("teacherName")
    val teacherName: String,
    @SerializedName("type")
    val type: String,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : Marker