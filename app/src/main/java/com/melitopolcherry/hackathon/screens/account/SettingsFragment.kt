package com.melitopolcherry.hackathon.screens.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.databinding.FragmentAccountSettingsBinding
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.screens.account.adapter.SettingsAdapter
import com.melitopolcherry.hackathon.screens.account.model.SettingHeaderUiModel
import com.melitopolcherry.hackathon.screens.account.model.SettingSimpleUiModel
import com.melitopolcherry.hackathon.screens.account.model.SettingsSwitchUiModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentAccountSettingsBinding>() {

    @Inject
    lateinit var accountManager: IAccountManager

    private val adapter by lazy {
        SettingsAdapter()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAccountSettingsBinding {
        return FragmentAccountSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        rvSettings.adapter = adapter
        adapter.items = listOf(SettingHeaderUiModel("Налаштування"))
            .plus(SettingSimpleUiModel("Авторизуватись"))
            .plus(SettingSimpleUiModel("Мова"))
            .plus(SettingHeaderUiModel("Сповіщення"))
            .plus(SettingsSwitchUiModel("Push - сповіщення"))
            .plus(SettingSimpleUiModel("Час"))
            .plus(SettingSimpleUiModel("Звук"))
            .plus(SettingHeaderUiModel("Геолокація"))
            .plus(SettingsSwitchUiModel("Доступ до геолокації"))
    }

    companion object {
        const val TAG = "SettingsFragment"
    }
}