package com.melitopolcherry.hackathon.data.models.realmModels

import io.realm.RealmObject

open class RealmLocation() : RealmObject() {

    constructor(latitude: Double, longitude: Double) : this() {
        this.latitude = latitude
        this.longitude = longitude
    }

    var latitude: Double? = null
    var longitude: Double? = null

}