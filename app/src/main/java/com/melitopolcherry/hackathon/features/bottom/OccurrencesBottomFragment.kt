package com.melitopolcherry.hackathon.features.bottom

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.DetailsDatesAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.DetailsOccurrencesCallback
import com.melitopolcherry.hackathon.adapters.cities_section.CitiesAdapter
import com.melitopolcherry.hackathon.adapters.cities_section.CitiesSection
import com.melitopolcherry.hackathon.adapters.cities_section.SectionedItemCallback
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.data.models.City
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.data.models.event.ocurrences.OccurrencesItem
import com.melitopolcherry.hackathon.databinding.BottomSheetDetailsDatesBinding
import com.melitopolcherry.hackathon.features.eventdetails.activity.DetailsEventActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OccurrencesBottomFragment(
    private val event: EventFull,
    private val occurrenceSelectedCallback: DetailsOccurrencesCallback.Callback
) : BaseBottomFragmentDialog(), SearchView.OnQueryTextListener {

    lateinit var binding: BottomSheetDetailsDatesBinding
    private val viewModel: OccurrencesViewModel by viewModels()

    private var recyclerHeight = 0
    private var locationEditText: EditText? = null
    private var adapter: DetailsDatesAdapter? = null
    private var searchCityAdapter: CitiesAdapter? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event = event
        viewModel.processError.observe(this) {
            if (activity is MainActivity) {
                (activity as MainActivity).processError(it) {}
            } else {
                (activity as DetailsEventActivity).processError(it) {}
            }
        }
        viewModel.dismissView.observe(this) {
            if (it) {
                dismiss()
            }
        }
        viewModel.reloadAdapter.observe(this) {
            if (it) {
                reloadAdapterAfterSearch()
            }
        }
        viewModel.setOccurrences.observe(this) {
            adapter?.setData(it)
            if (it == null || it.isEmpty()) {
                showNoDataLayout(true)
            } else {
                binding.datesRecyclerView.adapter = adapter
                showNoDataLayout(false)
            }
        }
        viewModel.setCities.observe(this) {
            if (it.isNotEmpty()) {
                showNoDataLayout(false)
                searchCityAdapter = CitiesAdapter(object : SectionedItemCallback.Callback {
                    override fun onClick(it: View, city: City) {
                        onSearchItemSelected(city)
                    }
                })
                searchCityAdapter?.setData(
                    CitiesSection(
                        "",
                        locationEditText?.text?.trim().toString(),
                        it
                    )
                )
                binding.datesRecyclerView.adapter = searchCityAdapter
            } else {
                showNoDataLayout(true)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDetailsDatesBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getEventOccurrences()
        adapter = DetailsDatesAdapter { item -> itemSelected(item) }

        binding.datesRecyclerView.post {
            recyclerHeight = binding.datesRecyclerView.height
            adapter?.recyclerHeight = recyclerHeight
        }

        binding.datesRecyclerView.setHasFixedSize(true)

        viewModel.layoutManager = LinearLayoutManager(requireContext())
        binding.datesRecyclerView.layoutManager = viewModel.layoutManager
        binding.datesRecyclerView.addOnScrollListener(viewModel.scrollListener)
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_details_dates, null)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.skipCollapsed = true
            dialog.setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val text = newText?.trim()
        viewModel.searchCities(text)
        return true
    }

    private fun onSearchItemSelected(city: City) {
        viewModel.getLocationByCity(city)
    }

    private fun itemSelected(occurrencesItem: OccurrencesItem) {
        occurrenceSelectedCallback.onClick(occurrencesItem)
        dismiss()
    }

    private fun showNoDataLayout(show: Boolean) {
        if (show) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.datesRecyclerView.visibility = View.INVISIBLE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.datesRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun reloadAdapterAfterSearch() {
        binding.datesRecyclerView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance(
            event: EventFull,
            occurrenceSelectedCallback: DetailsOccurrencesCallback.Callback
        ) =
            OccurrencesBottomFragment(event, occurrenceSelectedCallback)

        const val TAG = "OccurrencesBottomFragment"
    }
}