package com.melitopolcherry.hackathon.data.models.login

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AccessToken : RealmObject() {

    @PrimaryKey
    var id: Int? = null

    @field:SerializedName("access_token")
    var accessToken: String? = null

    @field:SerializedName("refresh_token")
    var refreshToken: String? = null

    @field:SerializedName("scope")
    var scope: String? = null

    @field:SerializedName("token_type")
    var tokenType: String? = null

    @field:SerializedName("expires_in")
    var expiresIn: Int? = null

    @field:SerializedName("jti")
    var jti: String? = null
}