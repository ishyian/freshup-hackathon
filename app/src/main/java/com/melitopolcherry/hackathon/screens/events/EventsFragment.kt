package com.melitopolcherry.hackathon.screens.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.model.EventItem
import com.melitopolcherry.hackathon.databinding.FragmentEventsBinding
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EventsFragment : BaseFragment<FragmentEventsBinding>(), EventsListAdapter.OnListItemSelectedListener {

    @Inject
    lateinit var accountManager: IAccountManager

    private val adapter by lazy {
        EventsListAdapter(this)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEventsBinding {
        return FragmentEventsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        rvEvents.adapter = adapter
        lifecycleScope.launch {
            val group = accountManager.fetchFaculty()?.group
            val events = (requireActivity() as MainActivity).events
            val filtered = events.filter { it.group == group }
            adapter.setData(filtered)
        }
        Unit
    }

    override fun itemSelected(event: EventItem) {

    }

    companion object {
        const val TAG = "EventsFragment"
    }
}