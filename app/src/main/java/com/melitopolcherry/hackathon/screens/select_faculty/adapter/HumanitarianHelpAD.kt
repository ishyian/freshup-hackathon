package com.melitopolcherry.hackathon.screens.select_faculty.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.melitopolcherry.hackathon.databinding.ItemCategoryBinding
import com.melitopolcherry.hackathon.databinding.ItemEventNotificationBinding
import com.melitopolcherry.hackathon.databinding.ItemHumanitarianBinding
import com.melitopolcherry.hackathon.screens.events.CategoryType
import com.melitopolcherry.hackathon.screens.notifications.model.NotificationUiModel

fun humanitarianAD() = adapterDelegateViewBinding<HumanitarianUiModel, Any, ItemHumanitarianBinding>(
    { layoutInflater, parent ->
        ItemHumanitarianBinding.inflate(layoutInflater, parent, false)
    }
) {
    bind {
        binding.textDescription.text = item.description
        binding.textAllcount.text = "(${item.allCount})"
        binding.textHelpCount.text = "${item.helpCount} разів"

    }
}