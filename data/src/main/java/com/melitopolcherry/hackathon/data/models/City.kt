package com.melitopolcherry.hackathon.data.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class City() : RealmObject() {

    @SerializedName("city_name")
    var name: String? = null

    @SerializedName("country_name")
    var country: String? = null

    @SerializedName("id")
    var cityId: String? = null

    @SerializedName("latitude")
    var latitude: Double? = null

    @SerializedName("longitude")
    var longitude: Double? = null

    @SerializedName("region")
    var region: String? = null

    constructor(id: String?, name: String, latitude: Double?, longitude: Double?) : this() {
        this.cityId = id
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(name: String, latitude: Double?, longitude: Double?) : this() {
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(name: String) : this() {
        this.name = name
    }
}
