package com.melitopolcherry.hackathon.data.models.realmModels

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmOnBoardingFlag() : RealmObject() {

    constructor(flag: Boolean) : this() {
        this.isPassed = flag
    }

    @PrimaryKey
    var id: Int? = null
    var isPassed: Boolean? = false
}