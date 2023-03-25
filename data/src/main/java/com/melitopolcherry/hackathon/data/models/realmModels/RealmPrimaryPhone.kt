package com.melitopolcherry.hackathon.data.models.realmModels

import com.melitopolcherry.hackathon.data.models.Phone
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmPrimaryPhone : RealmObject() {

    @PrimaryKey
    var primaryKey: Int? = null
    var phone: Phone? = null
}