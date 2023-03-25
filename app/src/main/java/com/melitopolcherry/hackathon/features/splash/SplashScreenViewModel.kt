package com.melitopolcherry.hackathon.features.splash

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val accountManager: IAccountManager
) : BaseViewModel() {

    val nextScreen: MutableLiveData<Int> = MutableLiveData()

    init {
        checkIsOnboardingPassed()
    }

    private fun checkIsOnboardingPassed() {
        if (accountManager.isOnBoardingPassed != null && !accountManager.isOnBoardingPassed!!) {
            nextScreen.postValue(1)
        } else {
            if(accountManager.isDataFilled()){
                nextScreen.postValue(1)
            } else {
                nextScreen.postValue(1)
            }
        }
    }
}