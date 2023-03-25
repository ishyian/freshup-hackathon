package com.melitopolcherry.timester.core.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

open class ListenableAsyncListDifferDelegationAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>) :
    AsyncListDifferDelegationAdapter<T>(diffCallback) {

    var areItemsActual: Boolean = true
        private set
    private val callbacks = mutableListOf<(List<T>) -> Unit>()

    override fun setItems(items: List<T>?) {
        areItemsActual = false
        differ.submitList(items) {
            areItemsActual = true
            callbacks.forEach { it(getItems()) }
            callbacks.clear()
        }
    }

    fun setItems(items: List<T>?, action: (List<T>) -> Unit) {
        setItems(items)
        onItemsActual(action)
    }

    fun onItemsActual(action: (List<T>) -> Unit) {
        if (areItemsActual) {
            action(items)
        } else {
            callbacks.add(action)
        }
    }
}