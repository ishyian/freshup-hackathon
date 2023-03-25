package com.melitopolcherry.timester.core.adapter

import androidx.recyclerview.widget.DiffUtil
import kotlin.jvm.internal.Intrinsics

abstract class BaseDiffCallback : DiffUtil.ItemCallback<Any>() {

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return Intrinsics.areEqual(oldItem, newItem)
    }
}