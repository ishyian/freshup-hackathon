package com.melitopolcherry.hackathon.features.maps.home.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.callbacks.LoadNextPageCallback
import com.melitopolcherry.hackathon.adapters.callbacks.RefreshCallback
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.livedata.FiltersConfigLiveData
import com.melitopolcherry.hackathon.databinding.FragmentMapHomeBinding
import com.melitopolcherry.hackathon.features.eventlist.EventListFragment
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.features.maps.core.presentation.ICameraPositionCallback
import com.melitopolcherry.hackathon.features.maps.core.presentation.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MapHomeFragment : BaseFragment<FragmentMapHomeBinding>(), View.OnClickListener, Observer<EventsFilters>,
    ICameraPositionCallback, RefreshCallback, LoadNextPageCallback {

    private val viewModel: MapHomeViewModel by viewModels()
    private var currentLocation: LatLng? = null
    private var filtersConfigLiveData: FiltersConfigLiveData? = null

    private var filtersHeight = 0
    private var showEventList = true
    private var placeWasChanged = false

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMapHomeBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }

    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun addFragment(
        fragment: Fragment,
        tag: String? = null
    ) {
        childFragmentManager.commit {
            add(R.id.content_main_map, fragment, tag)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listButton.isSelected = true

        binding.filtersFragment.post {
            filtersHeight = binding.filtersFragment.height
        }

        addFragment(MapFragment.newInstance(positionChangeCallback), MapFragment.TAG)

        binding.listButton.setOnClickListener(this)

        binding.searchHereButton.setOnClickListener(this)
        binding.myLocationButton.setOnClickListener(this)
    }

    override fun onChanged(t: EventsFilters) {

    }

    override val refreshCallback = object : RefreshCallback.Callback {
        override fun needToRefresh() {
            filtersConfigLiveData?.getFilters()?.value?.let {
                it.page = 0
                viewModel.searchEvents(it)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.listButton -> {
                if (showEventList) {
                    showEventList = false
                    binding.myLocationButton.visibility = View.INVISIBLE
                    binding.searchHereButton.visibility = View.GONE
                    binding.listButton.isSelected = false
                    if (childFragmentManager.findFragmentByTag(EventListFragment.TAG) == null) {
                        addFragment(
                            EventListFragment.newInstance(
                                filtersHeight,
                                refreshCallback,
                                loadNextPageCallback
                            ),
                            EventListFragment.TAG
                        )
                    }
                } else {
                    binding.listButton.isSelected = true
                    showEventList = true
                    if (placeWasChanged) {
                        binding.searchHereButton.visibility = View.VISIBLE
                    }
                    binding.myLocationButton.visibility = View.VISIBLE
                    if (childFragmentManager.findFragmentByTag(MapFragment.TAG) != null) {
                        childFragmentManager.commit {
                            replace(
                                R.id.content_main_map,
                                childFragmentManager.findFragmentByTag(MapFragment.TAG)!!,
                                MapFragment.TAG
                            )
                        }
                    } else {
                        childFragmentManager.commit {
                            replace(
                                R.id.content_main_map,
                                MapFragment.newInstance(positionChangeCallback),
                                MapFragment.TAG
                            )
                        }
                    }
                }
            }
            binding.myLocationButton -> {
                if (childFragmentManager.findFragmentByTag(MapFragment.TAG) != null) {
                    (childFragmentManager.findFragmentByTag(MapFragment.TAG) as MapFragment).moveToMyLocation()
                }
            }
            binding.searchHereButton -> {
                if (childFragmentManager.findFragmentByTag(MapFragment.TAG) != null) {
                    (childFragmentManager.findFragmentByTag(MapFragment.TAG) as MapFragment).searchInThisPlace()
                }
                placeWasChanged = false
                binding.searchHereButton.visibility = View.GONE
            }
        }
    }

    override val loadNextPageCallback = object : LoadNextPageCallback.Callback {
        override fun needToLoadNext(page: Int) {
            if (filtersConfigLiveData?.getFilters()?.value != null) {
                val eventsFiltersConfig: EventsFilters? =
                    if (filtersConfigLiveData?.getFilters()?.value == null) {
                        EventsFilters()
                    } else {
                        filtersConfigLiveData?.getFilters()?.value
                    }
                eventsFiltersConfig?.page = page
                filtersConfigLiveData?.getFilters()?.value = eventsFiltersConfig
            }
        }
    }

    override val positionChangeCallback = object : ICameraPositionCallback.Callback {
        override fun onChange(position: LatLng) {
            position.let {
                if (filtersConfigLiveData?.getFilters()?.value != null) {
                    val eventsFiltersConfig: EventsFilters? =
                        if (filtersConfigLiveData?.getFilters()?.value == null) {
                            EventsFilters()
                        } else {
                            filtersConfigLiveData?.getFilters()?.value
                        }
                    eventsFiltersConfig?.selectedCity = null
                    eventsFiltersConfig?.latitude = position.latitude.toString()
                    eventsFiltersConfig?.longitude = position.longitude.toString()
                    filtersConfigLiveData?.getFilters()?.value = eventsFiltersConfig

                    currentLocation = position
                }
            }
        }

        override fun showButton() {
            placeWasChanged = true
            binding.searchHereButton.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("map_zoom", 0.111)
        print("🗣 saved bind ")
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapHomeFragment()

        const val TAG = "MapHomeFragment"
    }
}