package com.melitopolcherry.hackathon.data.models.realmModels

import com.melitopolcherry.hackathon.data.utils.Enums
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmLoginMethod() : RealmObject() {

    constructor(method: Enums.LoginMethods) : this() {
        this.loginMethod = method.value
    }

    @PrimaryKey
    var primaryKey: Int? = null
    var loginMethod: Int? = null
}