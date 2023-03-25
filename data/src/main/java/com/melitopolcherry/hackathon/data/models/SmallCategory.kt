package com.melitopolcherry.hackathon.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmallCategory(
    @SerializedName("id")
    var categoryId: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("value")
    var value: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("color")
    var color: String? = null,
    @SerializedName("short_name")
    var shortName: String? = null,
    @SerializedName("icon_name")
    var imageName: String? = null,
) : Parcelable {

    fun categoryType() = when (type) {
        "main_category" -> CategoryType.MAIN_CATEGORY
        "all" -> CategoryType.ALL
        else -> CategoryType.SUB_CATEGORY
    }
}


