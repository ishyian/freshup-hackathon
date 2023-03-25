package com.melitopolcherry.hackathon.screens.account.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.melitopolcherry.hackathon.databinding.ItemSettingsHeaderBinding
import com.melitopolcherry.hackathon.screens.account.model.SettingHeaderUiModel

fun settingHeaderAD() = adapterDelegateViewBinding<SettingHeaderUiModel, Any, ItemSettingsHeaderBinding>(
    { layoutInflater, parent ->
        ItemSettingsHeaderBinding.inflate(layoutInflater, parent, false)
    }
) {
    bind {
        binding.textTitle.text = item.name
    }
}