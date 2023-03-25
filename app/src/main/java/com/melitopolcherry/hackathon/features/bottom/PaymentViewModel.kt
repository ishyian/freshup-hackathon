package com.melitopolcherry.hackathon.features.bottom

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.checkout.PaymentMethod
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val accountManager: IAccountManager
) : BaseViewModel() {
    var user: User? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dismissView: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val requiresAction: MutableLiveData<Boolean> = MutableLiveData()
    var redirectUrl: String? = null

    init {
        user = accountManager.fetchUser()
    }

    fun sendCard(paymentMethod: PaymentMethod) {
        Timber.d(paymentMethod.toString())
        println("🌟 flf,elf,efl,flef ${authToken?.value?.accessToken} ||| $paymentMethod")
        if (authToken?.value?.accessToken != null) {
            accountManager.sendCreditCard(authToken?.value?.accessToken!!, paymentMethod)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                               if (it.actionRequired != null && it.actionRequired!! && it.redirectUrl != null) {
                                   redirectUrl = it.redirectUrl
                                   requiresAction.postValue(true)
                               } else {
                                   user?.lastCardNums = paymentMethod.cardNumber?.subSequence(
                                       paymentMethod.cardNumber?.length!! - 4,
                                       paymentMethod.cardNumber?.length!!
                                   ).toString()
                                   accountManager.saveUser(user!!)
                                   requiresAction.postValue(false)
                               }
                           }, { e ->
                               processError.postValue(e)
                           })
        }
    }
}