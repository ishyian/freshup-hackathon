package com.melitopolcherry.hackathon.data.models.realmModels

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmFaculty() : RealmObject() {

    constructor(facultyId: String, group: String) : this() {
        this.facultyId = facultyId
        this.group = group
    }

    @PrimaryKey
    var id: Int? = null
    var facultyId: String? = null
    var group: String? = null
}