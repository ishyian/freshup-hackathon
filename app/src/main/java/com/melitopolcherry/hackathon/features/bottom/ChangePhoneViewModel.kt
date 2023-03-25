package com.melitopolcherry.hackathon.features.bottom

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.Phone
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ChangePhoneViewModel @Inject constructor(
    private val accountManager: IAccountManager
) : BaseViewModel() {

    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dismissView: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val showMessage: MutableLiveData<String> = MutableLiveData()
    val phoneChangedCallback: MutableLiveData<Boolean> = MutableLiveData()
    val phoneVerifiedCallback: MutableLiveData<Boolean> = MutableLiveData()

    var user: User? = null
    var formattedUserPhone: String? = ""

    var phoneChanged = false
    private var phoneVerified = false

    init {
        user = accountManager.fetchUser()
    }

    fun validateChangePhone(phone: String?) {
        if (phone?.isNotEmpty()!!) {
            if (phone != formattedUserPhone || (phone == formattedUserPhone && user?.phone?.verified == false)) {
                val phoneNumber = Phone("US", phone, "", true)
                accountManager.addPhoneNumber(authToken?.value?.accessToken!!, phoneNumber.toMap())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { isLoading.postValue(true) }
                    .doOnSuccess { isLoading.postValue(false) }
                    .doOnError { isLoading.postValue(false) }
                    .subscribe({
                        phoneNumber.id = it.id
                        user?.phone = phoneNumber
                        user?.phone?.verified = false
                        formattedUserPhone = phone
                        phoneChanged = true
                        accountManager.saveUser(user!!)
                        phoneChangedCallback.postValue(true)
                    }, { e ->
                        processError.postValue(e)
                    })
            } else {
                if (phone == formattedUserPhone && user?.phone?.verified != null && user?.phone?.verified!!) {
                    showMessage.postValue("This phone already validated")
                } else
                    if (phone.isEmpty()) {
                        showMessage.postValue("Phone number is empty")
                    }
            }
        }
    }

    fun sendCodeForConfirm(code: String, phone: String?) {
        if (code.isNotEmpty() && phone == formattedUserPhone) {
            accountManager.confirmPhoneCode(authToken?.value?.accessToken!!, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnComplete { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                    showMessage.postValue(
                        "Your phone validated"
                    )
                    user?.phone?.verified = true
                    accountManager.saveUser(user!!)
                    phoneVerified = true
                    phoneVerifiedCallback.postValue(true)
                }, { e ->
                    processError.postValue(e)
                })
        } else {
            if (phone != formattedUserPhone) {
                showMessage.postValue("You changed phone number")
            } else {
                showMessage.postValue("Code is empty")
            }
        }
    }

}