package com.melitopolcherry.hackathon.data.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Address() : RealmObject() {

    @PrimaryKey
    var primaryKey: Int? = null

    @field:SerializedName("country")
    var country: String? = null

    @field:SerializedName("street_address")
    var streetAddress: String? = null

    @field:SerializedName("city")
    var city: String? = null

    @field:SerializedName("postal_code")
    var postalCode: String? = null

    @field:SerializedName("region")
    var region: String? = null

    constructor(
        countryCode: String?,
        streetAddress: String?,
        city: String?,
        postalCode: String?,
        region: String?
    ) : this() {
        this.country = countryCode
        this.streetAddress = streetAddress
        this.city = city
        this.postalCode = postalCode
        this.region = region
    }
}