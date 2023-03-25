package com.melitopolcherry.hackathon.screens.notifications.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.melitopolcherry.hackathon.databinding.ItemEventNotificationHeaderBinding
import com.melitopolcherry.hackathon.screens.notifications.model.NotificationHeaderUiModel

fun notificationHeaderAD() =
    adapterDelegateViewBinding<NotificationHeaderUiModel, Any, ItemEventNotificationHeaderBinding>(
        { layoutInflater, parent ->
            ItemEventNotificationHeaderBinding.inflate(layoutInflater, parent, false)
        }
    ) {
        bind {
            binding.textTitle.text = item.name
        }
    }