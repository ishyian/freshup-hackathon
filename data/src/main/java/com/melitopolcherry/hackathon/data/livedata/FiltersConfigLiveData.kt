package com.melitopolcherry.hackathon.data.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FiltersConfigLiveData : LiveData<EventsFilters>() {

    private var liveFilterConfig = MutableLiveData<EventsFilters>()

    fun getFilters(): MutableLiveData<EventsFilters> {
        return liveFilterConfig
    }

    fun clearFilters(){
        val newConfig = EventsFilters()
        newConfig.latitude = liveFilterConfig.value?.latitude
        newConfig.longitude = liveFilterConfig.value?.longitude
        liveFilterConfig.value = newConfig
    }

    companion object {
        @JvmStatic
        val instance = FiltersConfigLiveData()
    }
}