package com.melitopolcherry.hackathon.data.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.data.model.Marker

class MarkersLiveData : LiveData<List<Marker>>() {

    private var liveData = MutableLiveData<List<String>>()

    fun getEvents(): MutableLiveData<List<String>> {
        return liveData
    }

    companion object {
        @JvmStatic
        val instance = MarkersLiveData()
    }
}