package com.melitopolcherry.hackathon.features.filters

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.custom_view.doOnApplyWindowInsets
import com.melitopolcherry.hackathon.databinding.FragmentFiltersBinding
import com.melitopolcherry.hackathon.features.main.MainActivity
import java.util.Calendar

class FiltersFragment : BaseFragment<FragmentFiltersBinding>(), View.OnClickListener {

    private var blockButtons = false
    private var tomorrow: Calendar? = null
    private var saturday: Calendar? = null
    private var sunday: Calendar? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentFiltersBinding.inflate(inflater, container, false)

    override fun onCreateView() {
        binding.filtersMain.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                top = padding.top + insets.systemWindowInsetTop,
            )
            insets
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tomorrow = Calendar.getInstance()
        tomorrow?.add(Calendar.DATE, 1)

        saturday = Calendar.getInstance()
        saturday?.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)

        sunday = Calendar.getInstance()
        sunday?.set(Calendar.DATE, saturday?.get(Calendar.DATE)!! + 1)

        binding.citiesButton.setOnClickListener(this)
        binding.timeButton.setOnClickListener(this)
        //binding.searchCard.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (!blockButtons) {
            blockButtons = true
            when (v) {
                binding.citiesButton -> {
                    CitiesBottomFragment.newInstance()
                        .show(childFragmentManager, CitiesBottomFragment.TAG)
                }
                binding.timeButton -> {
                    CalendarBottomFragment.newInstance()
                        .show(childFragmentManager, CalendarBottomFragment.TAG)
                }
                binding.searchCard -> {
                    (activity as MainActivity).supportFragmentManager.commit {
                        add(
                            R.id.map_home_container,
                            SearchFragment.newInstance(),
                            SearchFragment.TAG
                        )
                        addToBackStack(SearchFragment.TAG)
                    }
                }
            }
            runDelayedUnblock()
        }
    }

    private fun runDelayedUnblock() {
        Handler(Looper.getMainLooper()).postDelayed({
                                                        blockButtons = false
                                                    }, DELAY_IN_MS)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FiltersFragment()
        const val DELAY_IN_MS: Long = 900
        const val TAG = "FiltersFragment"
    }
}