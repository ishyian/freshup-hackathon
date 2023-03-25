package com.melitopolcherry.hackathon.features.filters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.RecentlySearchedAdapter
import com.melitopolcherry.hackathon.adapters.SearchAutocompleteAdapter
import com.melitopolcherry.hackathon.adapters.search_section.SearchAdapter
import com.melitopolcherry.hackathon.adapters.search_section.SearchSection
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.custom_view.doOnApplyWindowInsets
import com.melitopolcherry.hackathon.data.models.comprehensive.ComprehensiveResponse
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.data.models.comprehensive.TopItem
import com.melitopolcherry.hackathon.data.models.comprehensive.VenuesItem
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentSearchBinding
import com.melitopolcherry.hackathon.features.eventdetails.activity.DetailsEventActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.features.performer.LandingPagePerformerActivity
import com.melitopolcherry.hackathon.features.venue.LandingPageVenueActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(), View.OnClickListener {

    private val viewModel: SearchViewModel by viewModels()

    private var sectionAdapter: SearchAdapter? = null
    private var searchAutocompleteAdapter: SearchAutocompleteAdapter? = null
    private var recentlySearchedAdapter: RecentlySearchedAdapter? = null
    private var searchEditText: EditText? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSearchBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.querySelected.observe(this) {
            binding.searchView.setQuery(it, false)
        }
        viewModel.state.observe(this) {
            when (it) {
                SearchState.LOADING, SearchState.EMPTY, null -> {}
                SearchState.RECENTLY_SEARCHED -> {
                    if (recentlySearchedAdapter == null) {
                        recentlySearchedAdapter =
                            RecentlySearchedAdapter(viewModel.recentlySearchedCallback)
                    }
                    recentlySearchedAdapter?.setData(viewModel.recentlySearchResults)
                    binding.searchAutocompleteRecyclerView.adapter = recentlySearchedAdapter
                }
                SearchState.AUTOCOMPLETE -> {
                    if (searchAutocompleteAdapter == null) {
                        searchAutocompleteAdapter = SearchAutocompleteAdapter(viewModel.itemAutocompleteCallback)
                    }
                    searchAutocompleteAdapter?.setData(viewModel.autocompleteResults)
                    binding.searchAutocompleteRecyclerView.adapter = searchAutocompleteAdapter
                }
                SearchState.SEARCH_RESULTS -> {
                    if (sectionAdapter == null) {
                        sectionAdapter = SearchAdapter(viewModel.itemSearchCallback)
                    }
                    setDataToRecycler(
                        viewModel.searchResults!!,
                        searchEditText?.text?.trim().toString()
                    )
                    binding.searchRecyclerView.adapter = sectionAdapter
                }
            }
        }
        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
        viewModel.itemSelected.observe(this) {
            itemSelected(it)
        }
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchMain.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePadding(
                top = padding.top + insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom,
            )
            insets
        }

        binding.searchView.setOnQueryTextListener(viewModel)

        searchEditText =
            binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText?.textSize =
            resources.getDimension(R.dimen.search_text_size) / resources.displayMetrics.scaledDensity

        binding.backButton.setOnClickListener(this)

        searchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                viewModel.searchSuggestions(searchEditText?.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun itemSelected(it: Any?) {
        var intent = Intent()
        when (it) {
            is EventsItem -> {
                intent = Intent(requireContext(), DetailsEventActivity::class.java)
                intent.putExtra(Enums.BundleCodes.EventId.name, it.id)
            }
            is TopItem -> {
                intent = Intent(requireContext(), DetailsEventActivity::class.java)
                intent.putExtra(Enums.BundleCodes.EventId.name, it.id)
            }
            is VenuesItem -> {
                intent = Intent(requireContext(), LandingPageVenueActivity::class.java)
                intent.putExtra(Enums.BundleCodes.LandingId.name, it.id)
            }
            is PerformersItem -> {
                intent = Intent(requireContext(), LandingPagePerformerActivity::class.java)
                intent.putExtra(Enums.BundleCodes.LandingId.name, it.id)
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.backButton -> {
                hideKeyboard()
                (activity as MainActivity).onBackPressed()
            }
        }
    }

    private fun setDataToRecycler(it: ComprehensiveResponse, searchText: String) {
        println(
            "\n\n🤢 T: ${it.top?.size} \n E: ${it.events?.size}  \n" +
                " P: ${it.performers?.size}  \n" +
                " V: ${it.venues?.size}"
        )
        val array = arrayListOf<SearchSection>()
        it.top?.let {
            if (it.isNotEmpty()) {
                array.add(
                    SearchSection("Top Results", searchText, it)
                )
            }
        }
        it.performers?.let {
            if (it.isNotEmpty()) {
                array.add(
                    SearchSection("Performers", searchText, it)
                )
            }
        }
        it.events?.let {
            if (it.isNotEmpty()) {
                array.add(
                    SearchSection("Events", searchText, it)
                )
            }
        }
        it.venues?.let {
            if (it.isNotEmpty()) {
                array.add(
                    SearchSection("Venues", searchText, it)
                )
            }
        }
        sectionAdapter?.setData(array)
    }

    override fun onDetach() {
        println("⌛️onDetach")
        sectionAdapter = null
        super.onDetach()
    }

    override fun onDestroyView() {
        println("⌛️onDestroyView")
        searchEditText?.setOnEditorActionListener(null)
        searchEditText = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()

        const val TAG = "SearchFragment"
    }
}