package com.melitopolcherry.hackathon.features.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.melitopolcherry.hackathon.adapters.tracking_section.ItemTrackingCallback
import com.melitopolcherry.hackathon.adapters.tracking_section.TrackedSection
import com.melitopolcherry.hackathon.adapters.tracking_section.TrackingAdapter
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.data.models.comprehensive.VenuesItem
import com.melitopolcherry.hackathon.data.models.tracking.TrackedEntetiesResponse
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentProfileTrackingListBinding
import com.melitopolcherry.hackathon.features.eventdetails.activity.DetailsEventActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.features.performer.LandingPagePerformerActivity
import com.melitopolcherry.hackathon.features.trackingenteties.TrackingEntetiesActivity
import com.melitopolcherry.hackathon.features.venue.LandingPageVenueActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileTrackingListFragment : BaseFragment<FragmentProfileTrackingListBinding>(), ItemTrackingCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: ProfileTrackingViewModel by viewModels()
    private var sectionAdapter: TrackingAdapter? = null
    private var lastTrackResponse: TrackedEntetiesResponse? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileTrackingListBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.trackingResponse.observe(this) {
            if (it?.events != null || it?.performers != null || it?.venues != null) {
                setData(it)
            } else {
                showNoEventsText()
            }
        }
        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeToRefresh.setOnRefreshListener(this)

        sectionAdapter = TrackingAdapter()
        binding.trackingRecyclerView.adapter = sectionAdapter
        binding.trackingRecyclerView.setHasFixedSize(true)
    }

    override fun onRefresh() {
        binding.swipeToRefresh.isRefreshing = false
        viewModel.loadData()
    }

    private fun setData(it: TrackedEntetiesResponse?) {
        if (lastTrackResponse != it) {
            lastTrackResponse = it
            val array = arrayListOf<TrackedSection>()
            if (it?.events != null && it.events?.isNotEmpty()!!) {
                val venuesNotEmpty = it.venues?.isEmpty()!!
                array.add(TrackedSection("Events", venuesNotEmpty, "EventsItem", it.events!!))
            }
            if (it?.performers != null && it.performers?.isNotEmpty()!!) {
                val eventsNotEmpty = it.events?.isEmpty()!!
                array.add(
                    TrackedSection(
                        "Performers",
                        eventsNotEmpty,
                        "PerformersItem",
                        it.performers!!
                    )
                )
            }
            if (it?.venues != null && it.venues?.isNotEmpty()!!) {
                array.add(
                    TrackedSection("Venues", false, "VenuesItem", it.venues!!)
                )
            }
            showRecycler()
            sectionAdapter?.setItemClickListener(itemTrackingCallback)
            sectionAdapter?.setData(array)
        } else if (it?.events == null || it.events?.size == 0) {
            showNoEventsText()
        }
    }

    override val itemTrackingCallback = object : ItemTrackingCallback.Callback {
        override fun onItemClick(item: Any) {
            var intent = Intent()
            when (item) {
                is EventsItem -> {
                    intent = Intent(requireContext(), DetailsEventActivity::class.java)
                    intent.putExtra(Enums.BundleCodes.EventId.name, item.id)
                    intent.putExtra(Enums.BundleCodes.TicketGroupId.name, item.ticketEvolutionId)
                }
                is VenuesItem -> {
                    intent = Intent(requireContext(), LandingPageVenueActivity::class.java)
                    intent.putExtra(Enums.BundleCodes.LandingId.name, item.id)

                }
                is PerformersItem -> {
                    intent = Intent(requireContext(), LandingPagePerformerActivity::class.java)
                    intent.putExtra(Enums.BundleCodes.LandingId.name, item.id)
                }
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        override fun showAllEvents(type: String) {
            var intent = Intent()
            when (type) {
                "EventsItem" -> {
                    intent = Intent(requireContext(), TrackingEntetiesActivity::class.java)
                }
                "VenuesItem" -> {
                    intent = Intent(requireContext(), TrackingEntetiesActivity::class.java)

                }
                "PerformersItem" -> {
                    intent = Intent(requireContext(), TrackingEntetiesActivity::class.java)
                }
            }
            intent.putExtra(Enums.BundleCodes.EntityType.name, type)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    private fun showNoEventsText() {
        binding.noTrackLayout.root.visibility = View.VISIBLE
        binding.trackingRecyclerView.visibility = View.INVISIBLE
    }

    private fun showRecycler() {
        binding.noTrackLayout.root.visibility = View.GONE
        binding.trackingRecyclerView.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileTrackingListFragment()

        const val TAG = "ProfileTrackingListFragment"
    }
}