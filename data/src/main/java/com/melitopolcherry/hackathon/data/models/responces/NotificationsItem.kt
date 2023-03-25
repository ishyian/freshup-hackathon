package com.melitopolcherry.hackathon.data.models.responces

import com.google.gson.annotations.SerializedName

data class NotificationsItem(

    @field:SerializedName("read")
    val read: Boolean? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("text")
    val text: String? = null
)