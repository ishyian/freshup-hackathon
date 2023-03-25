package com.melitopolcherry.hackathon.data.models.realmModels

import com.melitopolcherry.hackathon.data.model.University
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmUniversity() : RealmObject() {

    constructor(universityId: String) : this() {
        this.universityId = universityId
    }

    @PrimaryKey
    var id: Int? = null
    var universityId: String? = null
}