package com.melitopolcherry.hackathon.features.onboarding

import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val accountManager: IAccountManager
) : BaseViewModel() {

    fun saveOnBoardingFlag() {
        accountManager.saveOnBoardingFlag(true)
    }
}