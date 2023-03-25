package com.melitopolcherry.hackathon.features.eventlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.melitopolcherry.hackathon.adapters.EventsListAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.LoadNextPageCallback
import com.melitopolcherry.hackathon.adapters.callbacks.RefreshCallback
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.livedata.EventsLiveData
import com.melitopolcherry.hackathon.data.livedata.FiltersConfigLiveData
import com.melitopolcherry.hackathon.data.models.event.small.EventMap
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentEventsListBinding
import com.melitopolcherry.hackathon.features.eventdetails.activity.DetailsEventActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EventListFragment :
    BaseFragment<FragmentEventsListBinding>(), Observer<List<EventMap>>,
    EventsListAdapter.OnEventListItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: EventListViewModel by viewModels()
    private var eventsListAdapter: EventsListAdapter? = null

    var refreshCallback: RefreshCallback.Callback? = null
    var loadNextPageCallback: LoadNextPageCallback.Callback? = null

    private var filtersHeight: Int = 0
    private var liveData: EventsLiveData? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEventsListBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        liveData = EventsLiveData.instance
        liveData?.getEvents()?.observe(this, this)

        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.refreshCallback = refreshCallback
        viewModel.loadNextPageCallback = loadNextPageCallback
    }

    override fun onResume() {
        viewModel.checkToken()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.eventListMain.setPadding(0, filtersHeight, 0, 0)
        binding.emptyStateLayout.root.setPadding(0, filtersHeight, 0, 0)

        eventsListAdapter = EventsListAdapter(this)

        setupViews()

        binding.swipeToRefresh.setOnRefreshListener(this)
        binding.emptyStateLayout.clearFiltersButton.setOnClickListener {
            FiltersConfigLiveData.instance.clearFilters()
        }
        binding.eventsRecyclerView.addOnScrollListener(viewModel.scrollListener)
        binding.eventsRecyclerView.setItemViewCacheSize(50)
    }

    private fun setupViews() {
        viewModel.layoutManager = LinearLayoutManager(requireContext())
        binding.eventsRecyclerView.layoutManager = viewModel.layoutManager
        eventsListAdapter = EventsListAdapter(this)
        binding.eventsRecyclerView.adapter = eventsListAdapter

        liveData?.getEvents()?.value?.let {
            eventsListAdapter?.setData(it)
        }
    }

    override fun onRefresh() {
        viewModel.page = 0
        viewModel.canLoad = true
        binding.swipeToRefresh.isRefreshing = false
        viewModel.isLoading.postValue(true)
        binding.emptyStateLayout.root.visibility = View.GONE
        binding.eventsRecyclerView.visibility = View.INVISIBLE
        viewModel.refreshCallback?.needToRefresh()
    }

    override fun onChanged(list: List<EventMap>?) {
        Timber.d("🍫 LIST: ${list?.size}, ${viewModel.page}. ${liveData?.page}")
        liveData?.page?.let {
            viewModel.page = it
        }
        if (viewModel.page == 0) {
            viewModel.isLoading.postValue(false)
        } else {
            viewModel.isLoadingMore.postValue(false)
        }
        if (list != null && list.isNotEmpty()) {
            if (binding.eventsRecyclerView.visibility != View.VISIBLE) {
                binding.emptyStateLayout.root.visibility = View.GONE
                binding.eventsRecyclerView.visibility = View.VISIBLE
            }
            if (viewModel.page == 0) {
                eventsListAdapter?.setData(list)
                binding.eventsRecyclerView.adapter = eventsListAdapter
                viewModel.canLoad = list.size >= 10
            } else {
                eventsListAdapter?.addData(list)
            }
        } else {
            if (viewModel.page == 0) {
                showNoEventsText()
            }
        }
    }

    override fun itemSelected(event: EventMap) {
        val intent = Intent(requireContext(), DetailsEventActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra(Enums.BundleCodes.EventId.name, event.id)
        intent.putExtra(Enums.BundleCodes.TicketGroupId.name, event.providedIds)
        startActivity(intent)
    }

    private fun showNoEventsText() {
        binding.emptyStateLayout.root.visibility = View.VISIBLE
        binding.eventsRecyclerView.visibility = View.INVISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance(
            filtersHeight: Int,
            refreshCallback: RefreshCallback.Callback,
            loadNextPageCallback: LoadNextPageCallback.Callback
        ) =
            EventListFragment().apply {
                this.filtersHeight = filtersHeight
                this.refreshCallback = refreshCallback
                this.loadNextPageCallback = loadNextPageCallback
            }

        const val TAG = "EventListFragment"
    }
}