package com.melitopolcherry.hackathon.data.models.realmModels

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmRecentlySearch() : RealmObject() {

    constructor(list: List<String>) : this() {
        val realmList: RealmList<String> = RealmList()
        realmList.addAll(list)
        this.listOfRecentlySearch = realmList
    }

    @PrimaryKey
    var primaryKey: Int? = null
    var listOfRecentlySearch: RealmList<String>? = null
}