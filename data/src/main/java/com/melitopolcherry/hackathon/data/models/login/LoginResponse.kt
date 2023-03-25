package com.melitopolcherry.hackathon.data.models.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("access_token")
    var accessToken: String? = null,

    @field:SerializedName("refresh_token")
    var refreshToken: String? = null,

    @field:SerializedName("scope")
    var scope: String? = null,

    @field:SerializedName("token_type")
    var tokenType: String? = null,

    @field:SerializedName("expires_in")
    var expiresIn: Int? = null,

    @field:SerializedName("jti")
    var jti: String? = null,

    @field:SerializedName("user")
    var user: User? = null
) {

    fun separateToken(): AccessToken {
        val token = AccessToken()
        token.accessToken = accessToken
        token.refreshToken = refreshToken
        token.tokenType = tokenType
        token.expiresIn = expiresIn
        token.jti = jti
        token.scope = scope
        return token
    }
}