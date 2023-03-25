package com.melitopolcherry.hackathon.data.models.checkout

import com.google.gson.annotations.SerializedName

data class LockResponce(
    @field:SerializedName("lock_id")
    var lockId: String? = null,

    @field:SerializedName("lock_acquired")
    var lockAcquired: Boolean? = null,

    @field:SerializedName("lock_price")
    var lockPrice: Float? = null,

    @field:SerializedName("lock_duration")
    var lockDuration: Int? = null,
)
