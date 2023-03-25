package com.melitopolcherry.hackathon.data.models.responces

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.login.User

data class ChangeEmailResponse(

    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("refresh_token")
    val refreshToken: String? = null,

    @field:SerializedName("scope")
    val scope: String? = null,

    @field:SerializedName("payment_method_last_four")
    var lastCardNums: String? = null,

    @field:SerializedName("token_type")
    val tokenType: String? = null,

    @field:SerializedName("expires_in")
    val expiresIn: Int? = null,

    @field:SerializedName("user")
    val user: User? = null,

    @field:SerializedName("jti")
    val jti: String? = null
)