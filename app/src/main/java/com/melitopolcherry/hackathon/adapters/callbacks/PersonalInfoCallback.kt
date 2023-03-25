package com.melitopolcherry.hackathon.adapters.callbacks

import com.melitopolcherry.hackathon.data.models.login.User

interface PersonalInfoCallback {

    val personalInfoCallback: Callback

    interface Callback {
        fun onReturned(updatedUser: User)
    }
}