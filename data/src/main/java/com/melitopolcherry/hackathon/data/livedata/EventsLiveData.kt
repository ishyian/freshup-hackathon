package com.melitopolcherry.hackathon.data.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.data.models.event.small.EventMap

class EventsLiveData : LiveData<List<EventMap>>() {

    private var liveData = MutableLiveData<List<EventMap>>()
    var page = 0

    fun getEvents(): MutableLiveData<List<EventMap>> {
        return liveData
    }

    companion object {
        @JvmStatic
        val instance = EventsLiveData()
    }
}