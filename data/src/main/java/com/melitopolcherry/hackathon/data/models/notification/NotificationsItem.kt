package com.melitopolcherry.hackathon.data.models.notification

import com.google.gson.annotations.SerializedName

data class NotificationsItem(
    @field:SerializedName("created_at")
    var createdAt: String? = null,

    @field:SerializedName("read")
    var read: Boolean? = null,

    @field:SerializedName("id")
    var id: String? = null,

    @field:SerializedName("text")
    var text: String? = null
)