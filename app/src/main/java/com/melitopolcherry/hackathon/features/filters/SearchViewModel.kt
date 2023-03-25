package com.melitopolcherry.hackathon.features.filters

import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.adapters.callbacks.AutocompleteCallback
import com.melitopolcherry.hackathon.adapters.callbacks.RecentlySearchedCallback
import com.melitopolcherry.hackathon.adapters.search_section.ItemSearchCallback
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.models.comprehensive.ComprehensiveResponse
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import com.melitopolcherry.hackathon.domain.managers.geoInfoProvider.IGeoInfoManager
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val geoInfoManager: IGeoInfoManager,
    private val accountManager: IAccountManager,
    private val eventsManager: IEventManager
) : BaseViewModel(), SearchView.OnQueryTextListener, ItemSearchCallback,
    AutocompleteCallback, RecentlySearchedCallback {

    private var autocompleteRequestHandler: Handler? = Handler(Looper.getMainLooper())
    private var autocompleteRequestRunnable = Runnable {}
    private var location: LatLng? = null
    var autocompleteResults: List<String> = listOf()
    var recentlySearchResults: List<String> = listOf()
    var searchResults: ComprehensiveResponse? = null

    val dismiss: MutableLiveData<Boolean> = MutableLiveData()
    val state: MutableLiveData<SearchState> = MutableLiveData()

    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val itemSelected: MutableLiveData<Any> = MutableLiveData()
    val querySelected: MutableLiveData<String> = MutableLiveData()

    init {
        location = accountManager.getLocation()
        recentlySearchResults = accountManager.getRecentlySearch()
        if (recentlySearchResults.isNotEmpty()) {
            state.postValue(SearchState.RECENTLY_SEARCHED)
        } else {
            state.postValue(SearchState.EMPTY)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val searchText = newText?.trim()

        if (searchText.isNullOrEmpty()) {
            state.postValue(SearchState.EMPTY)
            autocompleteRequestHandler?.removeCallbacks(autocompleteRequestRunnable)
            return true
        }

        if (searchText != querySelected.value) {
            searchAutoComplete(searchText)
        }

        return true
    }

    fun searchSuggestions(text: String?) {
        state.postValue(SearchState.LOADING)
        val filters = EventsFilters()
        filters.query = text
        val parameters = HashMap<String, Any?>()
        parameters["query"] = text
        parameters["fuzzy"] = true

        text?.let {
            accountManager.saveRecentlySearch(it)
        }
        //        if (location != null) { //todo when server fixed
        //            parameters["latitude"] = location?.latitude
        //            parameters["longitude"] = location?.longitude
        //        }

        eventsManager.searchComprehensive(parameters)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           searchResults = it
                           if (searchResults?.totalSize != null && searchResults?.totalSize!! > 0) {
                               state.postValue(SearchState.SEARCH_RESULTS)
                           } else {
                               state.postValue(SearchState.EMPTY)
                           }
                       }, { e ->
                           state.postValue(SearchState.EMPTY)
                           processError.postValue(e)
                       })
    }

    private fun searchAutoComplete(query: String) {
        state.postValue(SearchState.LOADING)
        autocompleteRequestHandler?.removeCallbacks(autocompleteRequestRunnable)
        autocompleteRequestRunnable = Runnable {
            eventsManager.searchAutocomplete(5, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                               autocompleteResults = it.options!!
                               if (autocompleteResults.isNotEmpty()) {
                                   state.postValue(SearchState.AUTOCOMPLETE)
                               } else {
                                   state.postValue(SearchState.EMPTY)
                               }
                           }, { e ->
                               state.postValue(SearchState.EMPTY)
                               processError.postValue(e)
                           })
        }
        autocompleteRequestHandler?.postDelayed(autocompleteRequestRunnable, 400)
    }

    override val itemSearchCallback = object : ItemSearchCallback.Callback {
        override fun onClick(item: Any) {
            itemSelected.postValue(item)
        }
    }

    override val itemAutocompleteCallback = object : AutocompleteCallback.Callback {
        override fun onClick(item: String) {
            querySelected.postValue(item)
            searchSuggestions(item)
        }
    }

    override fun onCleared() {
        autocompleteRequestHandler?.removeCallbacks(autocompleteRequestRunnable)
        super.onCleared()
    }

    override val recentlySearchedCallback = object : RecentlySearchedCallback.Callback {
        override fun onClick(item: String) {
            querySelected.postValue(item)
            searchSuggestions(item)
        }
    }
}

enum class SearchState {
    LOADING,
    EMPTY,
    RECENTLY_SEARCHED,
    AUTOCOMPLETE,
    SEARCH_RESULTS
}