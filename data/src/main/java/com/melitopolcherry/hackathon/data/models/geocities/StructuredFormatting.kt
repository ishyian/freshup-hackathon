package com.melitopolcherry.hackathon.data.models.geocities

import com.google.gson.annotations.SerializedName

data class StructuredFormatting(

    @field:SerializedName("secondary_text")
    val secondaryText: String? = null,

    @field:SerializedName("main_text")
    val mainText: String? = null
) {
    override fun toString(): String {
        return "StructuredFormatting(secondaryText=$secondaryText, mainText=$mainText)"
    }
}