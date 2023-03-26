package com.melitopolcherry.hackathon.features.maps.core.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.livedata.EventsLiveData
import com.melitopolcherry.hackathon.data.livedata.FiltersConfigLiveData
import com.melitopolcherry.hackathon.data.livedata.MarkersLiveData
import com.melitopolcherry.hackathon.data.livedata.VenuesLiveData
import com.melitopolcherry.hackathon.data.model.EventItem
import com.melitopolcherry.hackathon.data.model.FACULTIES
import com.melitopolcherry.hackathon.data.model.Marker
import com.melitopolcherry.hackathon.data.model.Place
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val accountManager: IAccountManager
) : BaseViewModel() {
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    var eventsLiveData = EventsLiveData.instance
    var markersLiveData = MarkersLiveData.instance
    var venuesLiveData = VenuesLiveData.instance

    private var _markers: MutableLiveData<List<Marker>> = MutableLiveData()
    val markers: LiveData<List<Marker>> = _markers

    var filtersConfigLiveData = FiltersConfigLiveData.instance

    var radius = 50f
    var userLocation: LatLng? = null

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    fun showMarkers(places: List<Place>, allowedTypes: List<String> = listOf()) {
        viewModelScope.launch {
            val markersList = arrayListOf<Marker>()
            var placesList = places
            if (allowedTypes.isNotEmpty()) {
                placesList = placesList.filter { allowedTypes.contains(it.type) }
            }
            markersList.addAll(placesList)
            Timber.d(markersList.size.toString())
            _markers.value = markersList
        }
    }
}