package com.melitopolcherry.hackathon.screens.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.databinding.FragmentEventNotificationsBinding
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.screens.notifications.adapter.NotificationsAdapter
import com.melitopolcherry.hackathon.screens.notifications.model.NotificationHeaderUiModel
import com.melitopolcherry.hackathon.screens.notifications.model.NotificationUiModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EventsNotificationsFragment : BaseFragment<FragmentEventNotificationsBinding>() {

    @Inject
    lateinit var accountManager: IAccountManager

    private val adapter by lazy {
        NotificationsAdapter()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEventNotificationsBinding {
        return FragmentEventNotificationsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        rvSettings.adapter = adapter
        adapter.items = listOf(NotificationHeaderUiModel("Сьогодні"))
            .plus(NotificationUiModel("Програмування з навчальною практикою", "22 хвилини назад", "Перенесено"))
            .plus(NotificationUiModel("Математичний аналіз", "34 хвилини назад", "Відмінено"))
            .plus(NotificationHeaderUiModel("Вчора"))
            .plus(NotificationUiModel("Дискретна математика", "11:12", ""))
            .plus(NotificationUiModel("Методика викладання фізики", "8:40", "Перенесено"))

    }

    companion object {
        const val TAG = "NotificationsFragment"
    }
}