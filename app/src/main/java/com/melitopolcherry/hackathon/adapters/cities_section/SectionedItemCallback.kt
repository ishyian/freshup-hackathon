package com.melitopolcherry.hackathon.adapters.cities_section

import android.view.View
import com.melitopolcherry.hackathon.data.models.City

interface SectionedItemCallback {

    val sectionedItemCallback: Callback

    interface Callback {
        fun onClick(it: View, city: City)
    }

}