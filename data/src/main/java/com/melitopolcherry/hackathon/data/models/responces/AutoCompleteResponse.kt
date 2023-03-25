package com.melitopolcherry.hackathon.data.models.responces

import com.google.gson.annotations.SerializedName

data class AutoCompleteResponse(
    @field:SerializedName("options")
    val options: List<String>? = null
)