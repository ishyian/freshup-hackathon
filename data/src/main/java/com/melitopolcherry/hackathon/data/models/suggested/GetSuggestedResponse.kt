package com.melitopolcherry.hackathon.data.models.suggested

import com.google.gson.annotations.SerializedName

data class GetSuggestedResponse(

    @field:SerializedName("totalItems")
    val totalItems: Int? = null,

    @field:SerializedName("size")
    val size: Int? = null,

    @field:SerializedName("totalPages")
    val totalPages: Int? = null,

    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("items")
    val items: List<SuggestedItem>? = null
)