package com.melitopolcherry.hackathon.features.bottom

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val accountManager: IAccountManager
) : BaseViewModel() {
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dismissView: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val showMessage: MutableLiveData<String> = MutableLiveData()

    fun validateUpdatePassword(
        oldPswd: String?,
        newPswd: String?,
        repeatPswd: String?
    ) {
        if (!oldPswd.isNullOrEmpty() && !newPswd.isNullOrEmpty() && !repeatPswd.isNullOrEmpty() && newPswd.length >= 8 && newPswd == repeatPswd) {
            updatePassword(oldPswd, newPswd, repeatPswd)
        } else {
            if (oldPswd.isNullOrEmpty()) {
                showMessage.postValue("Please fill old password")
            } else if (newPswd.isNullOrEmpty() && repeatPswd.isNullOrEmpty()) {
                showMessage.postValue("Please fill new password")
            } else if (!newPswd.isNullOrEmpty() && !repeatPswd.isNullOrEmpty() && newPswd.length < 8 && repeatPswd.length < 8) {
                showMessage.postValue(
                    "New password should be longer"
                )
            } else if (newPswd != repeatPswd) {
                showMessage.postValue("New password and repeat password are different")
            }
        }
    }

    private fun updatePassword(
        oldPswd: String,
        newPswd: String,
        repeatPswd: String
    ) {
        val map = HashMap<String, Any?>()
        map["old_password"] = oldPswd
        map["new_password"] = newPswd
        map["new_password_repeated"] = repeatPswd

        accountManager.updatePassword(authToken?.value?.accessToken!!, map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({
                           val accessToken = it.separateToken()
                           accountManager.saveToken(accessToken)
                           dismissView.postValue(true)
                       }, { e ->
                           processError.postValue(e)
                       })
    }

}