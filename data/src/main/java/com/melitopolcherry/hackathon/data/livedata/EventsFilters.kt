package com.melitopolcherry.hackathon.data.livedata

import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.models.CategoryType
import com.melitopolcherry.hackathon.data.models.City
import com.melitopolcherry.hackathon.data.models.EventCategory
import com.melitopolcherry.hackathon.data.models.EventSubCategory
import com.melitopolcherry.hackathon.data.models.SmallCategory
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Calendar

class EventsFilters {
    private val calendarToday = CalendarDay.today()

    var latitude: String? = null
    var longitude: String? = null
    var radius: Int = RADIUS_REGION_EVENTS_SEARCH

    var dates = listOf<CalendarDay>()
    var datesString = arrayListOf<String>()

    var query: String? = null

    var categories = arrayListOf<SmallCategory>()
    var mainCategoriesString = arrayListOf<String>()
    var subCategoriesString = arrayListOf<String>()

    var page: Int = 0
    private var size: Int = 50
    var selectedCity: City? = null

    fun toHashMap(): HashMap<String, Any> {
        val hashMap = HashMap<String, Any>()
        if (latitude != null) {
            hashMap["latitude"] = latitude!!
        }
        if (longitude != null) {
            hashMap["longitude"] = longitude!!
        }
        hashMap["radius"] = radius

        if (dates.isNotEmpty()) {
            datesString = arrayListOf()
            for (i in dates.indices) {
                val dateCalendar = dates[i]
                val date = Calendar.getInstance()
                date.set(Calendar.DAY_OF_MONTH, dateCalendar.day)
                date.set(Calendar.YEAR, dateCalendar.year)
                date.set(Calendar.MONTH, dateCalendar.month - 1)

                if (dateCalendar.year != calendarToday.year || dateCalendar.month != calendarToday.month || dateCalendar.day != calendarToday.day) {
                    date.set(Calendar.HOUR_OF_DAY, 0)
                    date.set(Calendar.MINUTE, 0)
                    date.set(Calendar.SECOND, 0)
                }
                datesString.add(DateHelper.backendDateFormatter.format(date.time))
            }
        } else {
            datesString.clear()
            val currentDay = DateHelper.backendDateFormatter.format(Calendar.getInstance().time)
            datesString.add(currentDay)
        }

        if (query != null) {
            hashMap["query"] = query!!
        }

        if (categories.isNotEmpty()) {
            mainCategoriesString.clear()
            subCategoriesString.clear()

            categories.forEach { smallCategory ->
                when (smallCategory.categoryType()) {
                    CategoryType.MAIN_CATEGORY -> {
                        mainCategoriesString.add(EventCategory.values().first { it.name == smallCategory.value }.name)
                    }
                    CategoryType.SUB_CATEGORY -> {
                        subCategoriesString.add(EventSubCategory.values().first { it.name == smallCategory.value }.name)
                    }
                    else -> {
                        //Ignore
                    }
                }
            }
        } else {
            mainCategoriesString.clear()
            subCategoriesString.clear()
        }
        hashMap["page"] = page
        hashMap["size"] = size
        return hashMap
    }

    override fun toString(): String {
        return "EventsFilters(latitude=$latitude, longitude=$longitude, radius=$radius, dateStart=$datesString, query=$query, category=$categories, mainCategorySS=$mainCategoriesString, subCategorySS=$subCategoriesString page=$page, perPage=$size)"
    }

    companion object {
        const val RADIUS_REGION_EVENTS_SEARCH = 20
        const val RADIUS_SIMILAR_EVENTS_SEARCH = 50
    }
}