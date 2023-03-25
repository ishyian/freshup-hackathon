package com.melitopolcherry.hackathon.features.eventlist

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.adapters.callbacks.LoadNextPageCallback
import com.melitopolcherry.hackathon.adapters.callbacks.RefreshCallback
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val eventsManager: IEventManager,
    private val accountManager: IAccountManager
) : BaseViewModel() {

    var refreshCallback: RefreshCallback.Callback? = null
    var loadNextPageCallback: LoadNextPageCallback.Callback? = null

    var user: User? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    var canLoad = true
    var page: Int = 0
    lateinit var layoutManager: LinearLayoutManager

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoadingMore: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
                if (canLoad) {
                    if ((layoutManager.childCount + layoutManager.findFirstVisibleItemPosition()) >= layoutManager.itemCount) {
                        canLoad = false
                        loadNextPage()
                    }
                }
            }
        }
    }

    private fun loadNextPage() {
        page++
        if (page != 0) {
            isLoadingMore.postValue(true)
//            binding.emptyStateLayout.visibility = View.GONE
        }
        loadNextPageCallback?.needToLoadNext(page)
    }

    fun checkToken() {
        if (authToken?.value == null) {
            val token = accountManager.getToken()
            if (token != null) {
                AuthTokenLiveData.instance.setToken(token)
            }
            this.authToken = AuthTokenLiveData.instance
        }
    }
}