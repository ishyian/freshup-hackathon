package com.melitopolcherry.hackathon.features.trackingenteties

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.AllTrackingEntetiesAdapter
import com.melitopolcherry.hackathon.core.presentation.BaseDataBindingActivity
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.data.models.comprehensive.VenuesItem
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.ActivityTrackingEntetiesBinding
import com.melitopolcherry.hackathon.features.eventdetails.activity.DetailsEventActivity
import com.melitopolcherry.hackathon.features.performer.LandingPagePerformerActivity
import com.melitopolcherry.hackathon.features.venue.LandingPageVenueActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingEntetiesActivity : BaseDataBindingActivity<ActivityTrackingEntetiesBinding>(),
    SearchView.OnQueryTextListener {
    override val layoutResId = R.layout.activity_tracking_enteties
    val viewModel: TrackingEntetiesViewModel by viewModels()

    private var adapter: AllTrackingEntetiesAdapter? = null
    private var searchEditText: EditText? = null

    override fun onCreate() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel.entityType = intent.extras?.getString(Enums.BundleCodes.EntityType.name)

        viewModel.processError.observe(this) {
            processError(it) {}
        }
        viewModel.showSearchData.observe(this) {
            if (it != null && it.isNotEmpty()) {
                adapter?.setData(it, viewModel.searchText)
                showRecyclerView()
            } else {
                showNoDataView()
            }
        }
        viewModel.reloadAfterSearch.observe(this) {
            if (it != null && it.isNotEmpty()) {
                adapter?.setData(it, "")
                showRecyclerView()
            } else {
                showNoDataView()
            }
        }
        viewModel.showData.observe(this) {
            if (it != null && it.isNotEmpty()) {
                if (viewModel.page == 0) {
                    adapter?.setData(it, "")
                } else {
                    adapter?.updateData(it, "")
                }
                showRecyclerView()
            } else {
                if (viewModel.page == 0) {
                    showNoDataView()
                }
            }
        }

        searchEditText =
            binding.searchTrackedEvents.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText?.textSize =
            resources.getDimension(R.dimen.search_text_size) / resources.displayMetrics.scaledDensity

        searchEditText?.hint = when (viewModel.entityType) {
            "EventsItem" -> {
                "Search Events"
            }
            "VenuesItem" -> {
                "Search Venues"
            }
            else -> {
                "Search Performers"
            }
        }

        searchEditText?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, 0)
                true
            } else {
                false
            }
        }

        adapter = AllTrackingEntetiesAdapter { item -> itemSelected(item) }
        viewModel.getTrackedEnteties()

        binding.searchTrackedEvents.setOnQueryTextListener(this)

        binding.backButton.root.setOnClickListener { finish() }

        binding.trackingEventsRv.adapter = adapter
        viewModel.layoutManager = LinearLayoutManager(this)
        binding.trackingEventsRv.layoutManager = viewModel.layoutManager

        binding.trackingEventsRv.addOnScrollListener(viewModel.scrollListener)
    }

    private fun itemSelected(item: Any) {
        var intent = Intent()
        when (viewModel.entityType) {
            "EventsItem" -> {
                item as EventsItem
                intent = Intent(this, DetailsEventActivity::class.java)
                intent.putExtra(Enums.BundleCodes.EventId.name, item.id)
                intent.putExtra(Enums.BundleCodes.TicketGroupId.name, item.ticketEvolutionId)
            }
            "VenuesItem" -> {
                item as VenuesItem
                intent = Intent(this, LandingPageVenueActivity::class.java)
                intent.putExtra(Enums.BundleCodes.LandingId.name, item.id)
            }
            "PerformersItem" -> {
                item as PerformersItem
                intent = Intent(this, LandingPagePerformerActivity::class.java)
                intent.putExtra(Enums.BundleCodes.LandingId.name, item.id)
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.searchTrackedEnteties(newText?.trim())
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    private fun showRecyclerView() {
        binding.trackingEventsRv.visibility = View.VISIBLE
        binding.noTrackingLayout.root.visibility = View.GONE
    }

    private fun showNoDataView() {
        binding.trackingEventsRv.visibility = View.INVISIBLE
        binding.noTrackingLayout.root.visibility = View.VISIBLE
    }
}