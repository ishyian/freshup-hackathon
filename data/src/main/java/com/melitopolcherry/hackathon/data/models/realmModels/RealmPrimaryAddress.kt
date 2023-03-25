package com.melitopolcherry.hackathon.data.models.realmModels

import com.melitopolcherry.hackathon.data.models.Address
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmPrimaryAddress : RealmObject() {

    @PrimaryKey
    var primaryKey: Int? = null
    var address: Address? = null
}