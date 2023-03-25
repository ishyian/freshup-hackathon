package com.melitopolcherry.hackathon.features.bottom

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.Address
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class DeliveryInfoViewModel @Inject constructor(
    private val accountManager: IAccountManager
) : BaseViewModel() {

    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dismissView: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val showMessage: MutableLiveData<String> = MutableLiveData()

    var user: User? = null
    var changedAddress: Address? = null

    var addressChanged = false
    private var addressVerified = false
    var phoneChanged = false
    private var phoneVerified = false

    init {
        user = accountManager.fetchUser()
    }

    fun sendAddressToServer(address: Address) {
        val addr = Address(
            address.country, address.streetAddress, address.city, address.postalCode,
            address.region
        )

        accountManager.addAddress(authToken?.value?.accessToken!!, addr)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({
                           user?.address = address
                           changedAddress = address
                           accountManager.saveUser(user!!)
                           addressVerified = true
                           if (addressVerified && phoneVerified || addressVerified && !phoneChanged) {
                               dismissView.postValue(true)
                           }
                       }, { e ->
                           processError.postValue(e)
                       })
    }
}