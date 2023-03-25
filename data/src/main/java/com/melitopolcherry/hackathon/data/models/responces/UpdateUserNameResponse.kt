package com.melitopolcherry.hackathon.data.models.responces

import com.google.gson.annotations.SerializedName
import com.melitopolcherry.hackathon.data.models.Address
import com.melitopolcherry.hackathon.data.models.Phone

data class UpdateUserNameResponse(

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("phone_number")
    var phone: Phone? = null,

    @field:SerializedName("address")
    var address: Address? = null,

    @field:SerializedName("authentication_provider")
    val authenticationProvider: String? = null,

    @field:SerializedName("second_name")
    val secondName: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)