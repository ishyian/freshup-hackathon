package com.melitopolcherry.hackathon.data.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Phone() : RealmObject() {

    @PrimaryKey
    var primaryKey: Int? = null

    @field:SerializedName("country_code")
    var countryCode: String? = null

    @field:SerializedName("verified")
    var verified: Boolean? = null

    @field:SerializedName("number")
    var number: String? = null

    @field:SerializedName("id")
    var id: String? = null

    @field:SerializedName("primary")
    var primary: Boolean? = null

    constructor(countryCode: String?, number: String?, id: String?, primary: Boolean?) : this() {
        this.countryCode = countryCode
        this.number = number
        this.id = id
        this.primary = primary
    }

    fun toMap(): HashMap<String, Any?> {
        val map = HashMap<String, Any?>()

        map["country_code"] = countryCode
        map["number"] = number
        map["primary"] = primary

        return map
    }

}