package com.melitopolcherry.hackathon.features.performer

import androidx.core.widget.NestedScrollView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.performer.PerformerDetails
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LandingPagePreformerViewModel @Inject constructor(
    private val accountManager: IAccountManager,
    private val eventsManager: IEventManager
) : BaseViewModel() {

    lateinit var layoutManager: LinearLayoutManager

    private var landingId: String? = null
    var performerDetails: PerformerDetails? = null
    private var nextRequestDate: String? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    private var size: Int = 10
    var page: Int = 0
    var canLoadNextPage = true

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoadingMore: MutableLiveData<Boolean> = MutableLiveData()
    val setUpData: MutableLiveData<PerformerDetails> = MutableLiveData()
    val setUpEvents: MutableLiveData<List<EventsItem>> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val showMessage: MutableLiveData<String> = MutableLiveData()
    val setTrackImage: MutableLiveData<Boolean> = MutableLiveData()

    fun getPerformer(landingId: String?) {
        this.landingId = landingId
        eventsManager.performerDetails(authToken?.value?.accessToken, landingId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({
                           performerDetails = it
                           setData(it.events)
                           setUpData.postValue(it)
                       }, { e ->
                           processError.postValue(e)
                       })
    }

    fun getEvents() {
        Timber.d("🎒 getEvents nextRequestDate $nextRequestDate")
        nextRequestDate?.let {
            eventsManager.loadPerformersEvents(
                authToken?.value?.accessToken,
                landingId!!,
                size,
                it
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoadingMore.postValue(true) }
                .doOnSuccess { isLoadingMore.postValue(false) }
                .doOnError { isLoadingMore.postValue(false) }
                .subscribe({ res ->
                               setData(res.events)
                           }, { e ->
                               processError.postValue(e)
                           })
        }
    }

    private fun setData(it: List<EventsItem>?) {
        if (it != null && it.isNotEmpty()) {
            setUpEvents.postValue(it)
            if (it.size >= size) {
                page += 1
                nextRequestDate = it[it.size - 1].startDate
                canLoadNextPage = true
            } else {
                nextRequestDate = null
                canLoadNextPage = false
            }
        } else {
            canLoadNextPage = false
            if (page == 0) {
                setUpEvents.postValue(null)
            }
        }
    }

    fun trackPerformer(token: String) {
        setTrackImage.postValue(true)
        performerDetails?.isTracked = true
        eventsManager.trackPerformer(token, landingId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { e ->
                setTrackImage.postValue(false)
                performerDetails?.isTracked = false
                processError.postValue(e)
            })
    }

    fun unTrackPerformer(token: String) {
        setTrackImage.postValue(false)
        performerDetails?.isTracked = false
        eventsManager.unTrackPerformer(token, landingId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { e ->
                setTrackImage.postValue(true)
                performerDetails?.isTracked = true
                processError.postValue(e)
            })
    }

    val scrollListener =
        NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (v?.getChildAt(v.childCount - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                    scrollY > oldScrollY
                ) {
                    if (canLoadNextPage) {
                        if ((layoutManager.childCount + layoutManager.itemCount) >= layoutManager.findFirstVisibleItemPosition()) {
                            canLoadNextPage = false
                            Timber.d("🚧Total Last Item Wow !")
                            getEvents()
                        }
                    }
                }
            }
        }
}