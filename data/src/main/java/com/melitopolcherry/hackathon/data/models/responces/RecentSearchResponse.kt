package com.melitopolcherry.hackathon.data.models.responces

import com.google.gson.annotations.SerializedName

data class RecentSearchResponse(

    @field:SerializedName("content")
    val content: List<String?>? = null
)