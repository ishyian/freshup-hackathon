package com.melitopolcherry.hackathon.features.seatingchart

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.melitopolcherry.hackathon.adapters.callbacks.StadiumFiltersCallback
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.models.SeaticsModel
import com.melitopolcherry.hackathon.data.models.TicketsFilter
import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.data.utils.Enums
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SeatingChartViewModel @Inject constructor() : ViewModel(), StadiumFiltersCallback {

    var ticketsFilter = TicketsFilter()
    var ticketGroups: List<TicketGroup> = arrayListOf()
    var selectedTicketGroups: ArrayList<TicketGroup> = arrayListOf()
    var lastScrollSelectedTicket: TicketGroup? = null
    val sectorColorsMap = hashMapOf<String, Int>()
    var setColors = true
    var stadiumState: Enums.StadiumState? = null
    var event: EventFull? = null
    var selectedTicketUrl: String? = null

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showSeatViewButton: MutableLiveData<Boolean> = MutableLiveData()
    val removeHighlight: MutableLiveData<Boolean> = MutableLiveData()
    val highlightTicket: MutableLiveData<TicketGroup> = MutableLiveData()
    val setData: MutableLiveData<List<TicketGroup>> = MutableLiveData()
    val setFilteredData: MutableLiveData<List<TicketGroup>> = MutableLiveData()

    fun initial() {
        setData.postValue(ticketGroups)
    }

    fun loadMap(width: Int, height: Int, hexColor: String): String {
        val request = "javascript:loadMap(\"${event?.title}\", \"${event?.venue?.name}\", ${
            DateHelper.dateForStadium(
                event?.startDate!!
            )
        }, [${setTicketsInStadium(ticketGroups)}], ${width}, ${height}, \"${hexColor}\")"
        Timber.d(request)
        return request
    }

    fun setSelectedTicket(ticket: TicketGroup?) {
        lastScrollSelectedTicket = ticket
        ticket?.let { highlightTicket.value = it }
        selectedTicketUrl = ticket?.url
        Timber.d(selectedTicketUrl.toString())
        if (selectedTicketUrl != null && selectedTicketUrl?.length!! > 5 && showSeatViewButton.value == false) {
            showSeatViewButton.value = true
        } else if (selectedTicketUrl == null || selectedTicketUrl == "" && showSeatViewButton.value == true) {
            showSeatViewButton.value = false
        }
    }

    fun getTicketsForSection(input: Array<String>?) {
        val sortedList = arrayListOf<TicketGroup>()

        input?.forEach { ticket ->
            try {
                val jObject = JSONObject(ticket)

                val section = jObject.getString("tgUserSec")
                val id = jObject.getInt("tgID")
                val seatUrl = jObject.getString("vfsUrlLarge")

                if (setColors) {
                    val color = jObject.getString("tgColor")
                    if (color != "undefined") {
                        sectorColorsMap[section] = Color.parseColor(color)
                    }
                }
                ticketGroups.forEach {
                    if (it.id == id) {
                        it.url = seatUrl
                        sortedList.add(it)
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        if (sectorColorsMap.isNotEmpty()) {
            setColors = false
        }
        sortTicketsAndShow(sortedList)
    }

    private fun sortTicketsAndShow(sortedList: ArrayList<TicketGroup>) {
        sortedList.sortBy { it.retailPrice }
        selectedTicketGroups = sortedList
        setData.postValue(sortedList)
    }

    fun setTicketsInStadium(tickets: List<TicketGroup>?): String {
        var stringBuilder = ""
        if (tickets?.size != null && tickets.isNotEmpty()) {
            for (i in tickets.indices) {
                val it = ticketGroups[i]
                stringBuilder += SeaticsModel(
                    it.section,
                    it.row,
                    it.availableQuantity,
                    it.retailPrice,
                    it.id
                )
                if (i < tickets.size - 1) {
                    stringBuilder += ", "
                }
            }
        }
        return stringBuilder
    }

    private fun filterTickets(filteredList: List<TicketGroup>) {
        println("🤖 js FILTERED '${filteredList}'")
        setFilteredData.value = filteredList
    }

    override val stadiumFiltersCallback = object : StadiumFiltersCallback.Callback {
        override fun onFilter(filteredList: List<TicketGroup>, filter: TicketsFilter) {
            ticketsFilter = filter
            filterTickets(filteredList)
        }
    }
}