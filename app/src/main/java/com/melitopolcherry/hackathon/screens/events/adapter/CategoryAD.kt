package com.melitopolcherry.hackathon.screens.events.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.melitopolcherry.hackathon.databinding.ItemCategoryBinding
import com.melitopolcherry.hackathon.databinding.ItemEventNotificationBinding
import com.melitopolcherry.hackathon.screens.events.CategoryType
import com.melitopolcherry.hackathon.screens.notifications.model.NotificationUiModel

fun categoryAD(onClick: (CategoryType) -> Unit) = adapterDelegateViewBinding<CategoryType, Any, ItemCategoryBinding>(
    { layoutInflater, parent ->
        ItemCategoryBinding.inflate(layoutInflater, parent, false)
    }
) {
    bind {
        binding.textTitle.text = item.value
    }
    itemView.setOnClickListener {
        onClick(item)
    }
}