package com.melitopolcherry.hackathon.data.models.geocities

import com.google.gson.annotations.SerializedName

data class PredictionsItem(

    @field:SerializedName("structured_formatting")
    val structuredFormatting: StructuredFormatting? = null,

    @field:SerializedName("place_id")
    val placeId: String? = null

) {
    override fun toString(): String {
        return "PredictionsItem(structuredFormatting=${structuredFormatting?.mainText}, placeId=$placeId)"
    }
}