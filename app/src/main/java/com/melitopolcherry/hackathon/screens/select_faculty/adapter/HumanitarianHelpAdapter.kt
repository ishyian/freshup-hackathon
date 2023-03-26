package com.melitopolcherry.hackathon.screens.select_faculty.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.melitopolcherry.hackathon.screens.events.CategoryType
import com.melitopolcherry.timester.core.adapter.BaseDiffCallback

class HumanitarianHelpAdapter(
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(humanitarianAD())
    }

    private companion object DiffCallback : BaseDiffCallback() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
            else -> oldItem.javaClass == newItem.javaClass
        }
    }
}