package com.melitopolcherry.hackathon.data.models.weather.current

import com.google.gson.annotations.SerializedName

data class Clouds(

    @field:SerializedName("all")
    val all: Int? = null
)