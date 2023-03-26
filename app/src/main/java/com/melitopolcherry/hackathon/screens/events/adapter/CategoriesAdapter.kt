package com.melitopolcherry.hackathon.screens.events.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.melitopolcherry.hackathon.screens.events.CategoryType
import com.melitopolcherry.timester.core.adapter.BaseDiffCallback

class CategoriesAdapter(
    onClick: (CategoryType) -> Unit
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(categoryAD(onClick))
    }

    private companion object DiffCallback : BaseDiffCallback() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
            else -> oldItem.javaClass == newItem.javaClass
        }
    }
}