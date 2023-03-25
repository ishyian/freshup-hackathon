package com.melitopolcherry.hackathon.data.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.data.models.venue.VenuesSearchItem

class VenuesLiveData : LiveData<List<VenuesSearchItem>>() {

    private var liveData = MutableLiveData<List<VenuesSearchItem>>()
    var page = 0

    fun getVenues(): MutableLiveData<List<VenuesSearchItem>> {
        return liveData
    }

    companion object {
        @JvmStatic
        val instance = VenuesLiveData()
    }
}