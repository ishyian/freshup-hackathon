package com.melitopolcherry.hackathon.features.ordersupcoming

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.order.GetOrdersResponse
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrdersUpcomingViewModel @Inject constructor(
    private val accountManager: IAccountManager,
    private val evensManager: IEventManager
) : BaseViewModel() {

    val tokenLiveData = AuthTokenLiveData.instance

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoadingMore: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val ordersResponse: MutableLiveData<GetOrdersResponse?> = MutableLiveData()

    var canLoadNextPage = true
    var currentPage = 0
    private var size = 5

    init {
        getOrders()
    }

    fun getOrders() {
        if (tokenLiveData.value?.accessToken != null) {
            evensManager.getUserOrders(tokenLiveData.value?.accessToken!!, currentPage, size)
                .doOnSubscribe {
                    if (currentPage == 0) {
                        isLoading.postValue(true)
                    } else {
                        isLoadingMore.postValue(true)
                    }
                }
                .doOnSuccess {
                    if (currentPage == 0) {
                        isLoading.postValue(false)
                    } else {
                        isLoadingMore.postValue(false)
                    }
                }
                .doOnError {
                    if (currentPage == 0) {
                        isLoading.postValue(false)
                    } else {
                        isLoadingMore.postValue(false)
                    }
                }
                .subscribe({
                               ordersResponse.postValue(it)
                           }, { e ->
                               ordersResponse.postValue(GetOrdersResponse(orders = listOf()))
                               processError.postValue(e)
                           })
        } else {
            ordersResponse.postValue(null)
        }
    }
}