package com.melitopolcherry.hackathon.data.models.realmModels

import com.melitopolcherry.hackathon.data.models.City
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmRecentlyCities() : RealmObject() {

    constructor(list: List<City>) : this() {
        val realmList: RealmList<City> = RealmList()
        realmList.addAll(list)
        this.listOfRecentlyCities = realmList
    }

    @PrimaryKey
    var primaryKey: Int? = null
    var listOfRecentlyCities: RealmList<City>? = null
}