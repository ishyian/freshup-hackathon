package com.melitopolcherry.hackathon.data.models.login

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.Address
import com.melitopolcherry.hackathon.data.models.Phone
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {

    @PrimaryKey
    @field:SerializedName("id")
    var id: String? = null

    @field:SerializedName("phone_number")
    var phone: Phone? = null

    @field:SerializedName("address")
    var address: Address? = null

    @field:SerializedName("payment_method_last_four")
    var lastCardNums: String? = null

    @field:SerializedName("image_url")
    var imageUrl: String? = null

    @field:SerializedName("second_name")
    var secondName: String? = null

    @field:SerializedName("first_name")
    var firstName: String? = null

    @field:SerializedName("email")
    var email: String? = null

    val fullName: String
        get() {
            var result = ""

            if (!TextUtils.isEmpty(firstName)) {
                result = firstName!!
            }

            if (!TextUtils.isEmpty(secondName)) {
                result = "$result $secondName"
            }

            return result
        }
}