package com.melitopolcherry.hackathon.screens.account.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.melitopolcherry.hackathon.databinding.ItemSettingsSimpleBinding
import com.melitopolcherry.hackathon.screens.account.model.SettingSimpleUiModel

fun settingSimpleAD() = adapterDelegateViewBinding<SettingSimpleUiModel, Any, ItemSettingsSimpleBinding>(
    { layoutInflater, parent ->
        ItemSettingsSimpleBinding.inflate(layoutInflater, parent, false)
    }
) {
    bind {
        binding.textTitle.text = item.name
    }
}