package com.melitopolcherry.hackathon.screens.account.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.melitopolcherry.hackathon.databinding.ItemSettingsCheckboxBinding
import com.melitopolcherry.hackathon.screens.account.model.SettingsSwitchUiModel

fun settingSwitchAD() = adapterDelegateViewBinding<SettingsSwitchUiModel, Any, ItemSettingsCheckboxBinding>(
    { layoutInflater, parent ->
        ItemSettingsCheckboxBinding.inflate(layoutInflater, parent, false)
    }
) {
    bind {
        binding.textTitle.text = item.name
    }
}