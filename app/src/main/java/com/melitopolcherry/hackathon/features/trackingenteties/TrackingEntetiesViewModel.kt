package com.melitopolcherry.hackathon.features.trackingenteties

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.tracking.event.TrackingEventsList
import com.melitopolcherry.hackathon.data.models.tracking.performer.TrackingPerformersList
import com.melitopolcherry.hackathon.data.models.tracking.venue.TrackingVenuesList
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TrackingEntetiesViewModel @Inject constructor(
    private val eventsManager: IEventManager
) : BaseViewModel() {
    var page: Int = 0
    var searchText: String? = null
    var entityType: String? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    private var size: Int = 15
    private var canLoadNextPage = true
    private var listOfTracking = arrayListOf<Any>()
    private var requestHandler = Handler(Looper.getMainLooper())
    private var requestRunnable = Runnable {}
    lateinit var layoutManager: LinearLayoutManager

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoadingMore: MutableLiveData<Boolean> = MutableLiveData()
    val reloadAfterSearch: MutableLiveData<List<Any>> = MutableLiveData()
    val showData: MutableLiveData<List<Any>?> = MutableLiveData()
    val showSearchData: MutableLiveData<List<Any>?> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    fun getTrackedEnteties() {
        when (entityType) {
            "EventsItem" -> {
                getTrackedEvents()
            }
            "VenuesItem" -> {
                getTrackedVenues()
            }
            "PerformersItem" -> {
                getTrackedPerformers()
            }
        }
    }

    fun searchTrackedEnteties(text: String?) {
        searchText = text
        if (text != null && text.isNotEmpty()) {
            requestHandler.removeCallbacks(requestRunnable)
            when (entityType) {
                "EventsItem" -> {
                    searchEvents(text)
                }
                "VenuesItem" -> {
                    searchVenues(text)
                }
                "PerformersItem" -> {
                    searchPerformers(text)
                }
            }
        } else {
            isLoading.postValue(false)
            requestHandler.removeCallbacks(requestRunnable)
            reloadAfterSearch()
        }
    }

    private fun getTrackedEvents() {
        eventsManager.getTrackedEvents(authToken?.value?.accessToken!!, page, size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                doOnSubscribe()
            }
            .doOnSuccess {
                doOnComplete()
            }
            .doOnError {
                doOnComplete()
            }
            .subscribe({
                setData(it)
            }, { e ->
                processError.postValue(e)
            })
    }

    private fun setData(it: Any) {
        var totalItems = 0
        var items = listOf<Any>()
        when (it) {
            is TrackingEventsList -> {
                totalItems = it.totalItems!!
                items = it.items!!
            }
            is TrackingPerformersList -> {
                totalItems = it.totalItems!!
                items = it.items!!
            }
            is TrackingVenuesList -> {
                totalItems = it.totalItems!!
                items = it.items!!
            }
        }
        if (items.isNotEmpty()) {
            when {
                listOfTracking.size >= totalItems -> {
                    canLoadNextPage = false
                }
                items.size == totalItems -> {
                    canLoadNextPage = false
                }
                else -> {
                    page += 1
                    canLoadNextPage = true
                }
            }
            if (page == 0) {
                listOfTracking.clear()
                listOfTracking.addAll(items)
            } else {
                listOfTracking.addAll(items)
            }
            showData.postValue(items)
        } else {
            if (page == 0) {
                showData.postValue(null)
            }
        }
    }

    private fun getTrackedVenues() {
        eventsManager.getTrackedVenues(authToken?.value?.accessToken!!, page, size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                doOnSubscribe()
            }
            .doOnSuccess {
                doOnComplete()
            }
            .doOnError {
                doOnComplete()
            }.subscribe({
                setData(it)
            }, { e ->
                processError.postValue(e)
            })
    }

    private fun getTrackedPerformers() {
        eventsManager.getTrackedPerformers(authToken?.value?.accessToken!!, page, size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                doOnSubscribe()
            }
            .doOnSuccess {
                doOnComplete()
            }
            .doOnError {
                doOnComplete()
            }
            .subscribe({
                setData(it)
            }, { e ->
                processError.postValue(e)
            })
    }

    private fun searchEvents(text: String) {
        requestRunnable = Runnable {
            eventsManager.searchInTrackedEvents(
                authToken?.value?.accessToken!!,
                text,
                size,
                0
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                    showSearchData.postValue(it.items)
                }, { e ->
                    processError.postValue(e)
                })
        }
        requestHandler.postDelayed(requestRunnable, 600)
    }

    private fun searchPerformers(text: String) {
        requestRunnable = Runnable {
            eventsManager.searchInTrackedPerformers(
                authToken?.value?.accessToken!!,
                text,
                size,
                0
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                    showSearchData.postValue(it.items)
                }, { e ->
                    processError.postValue(e)
                })
        }
        requestHandler.postDelayed(requestRunnable, 600)
    }

    private fun searchVenues(text: String) {
        requestRunnable = Runnable {
            eventsManager.searchInTrackedVenues(
                authToken?.value?.accessToken!!,
                text,
                size,
                0
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                    showSearchData.postValue(it.items)
                }, { e ->
                    processError.postValue(e)
                })
        }
        requestHandler.postDelayed(requestRunnable, 600)
    }

    private fun doOnSubscribe() {
        if (page == 0) {
            isLoading.postValue(true)
        } else {
            isLoadingMore.postValue(true)
        }
    }

    private fun doOnComplete() {
        if (page == 0) {
            isLoading.postValue(false)
        } else {
            isLoadingMore.postValue(false)
        }
    }

    private fun reloadAfterSearch() {
        reloadAfterSearch.postValue(listOfTracking)
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
                if (canLoadNextPage) {
                    if ((layoutManager.childCount + layoutManager.itemCount) >= layoutManager.findFirstVisibleItemPosition()) {
                        canLoadNextPage = false
                        getTrackedEnteties()
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        requestHandler.removeCallbacks(requestRunnable)
    }
}