package com.melitopolcherry.hackathon.screens.notifications.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.melitopolcherry.hackathon.databinding.ItemEventNotificationBinding
import com.melitopolcherry.hackathon.screens.notifications.model.NotificationUiModel

fun notificationAD() = adapterDelegateViewBinding<NotificationUiModel, Any, ItemEventNotificationBinding>(
    { layoutInflater, parent ->
        ItemEventNotificationBinding.inflate(layoutInflater, parent, false)
    }
) {
    bind {
        binding.textTitle.text = item.name
        binding.textStatus.text = item.status
        binding.textTime.text = item.time
    }
}