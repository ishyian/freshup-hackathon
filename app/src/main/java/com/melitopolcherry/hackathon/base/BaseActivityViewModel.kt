package com.melitopolcherry.hackathon.base

import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.registration.IRegistrationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BaseActivityViewModel @Inject constructor(
    val registrationManager: IRegistrationManager,
    val accountManager: IAccountManager
) : BaseViewModel() {

}