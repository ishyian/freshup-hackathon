package com.melitopolcherry.hackathon.features.filters

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.cities_section.CitiesAdapter
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.databinding.BottomSheetCitiesBinding
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CitiesBottomFragment : BaseBottomFragmentDialog(),
    SearchView.OnQueryTextListener {

    private val viewModel: CitiesViewModel by viewModels()
    lateinit var binding: BottomSheetCitiesBinding

    private var sectionAdapter: CitiesAdapter? = null
    private var searchEditText: EditText? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.dismiss.observe(this) {
            dismiss()
        }
        viewModel.showMessage.observe(this) {
            if (it) {
                Toast.makeText(
                    requireContext(), getString(R.string.no_city),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.showEmptyState.observe(this) {
            if (it) {
                showNoDataLayout()
            } else {
                showRecyclerView()
            }
        }
        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
        viewModel.addSection.observe(this) {
            sectionAdapter?.addData(it)
        }
        viewModel.setSections.observe(this) {
            if (it != null) {
                sectionAdapter?.setData(it)
            } else {
                sectionAdapter?.clear()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCitiesBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_cities, null)
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.locationSearchView.setOnQueryTextListener(this)
        searchEditText =
            binding.locationSearchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText?.textSize =
            resources.getDimension(R.dimen.search_text_size) / resources.displayMetrics.scaledDensity

        searchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        setupRecyclerView()
        viewModel.getRecentlyCities()
        viewModel.getNearCities()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val text = newText?.trim()
        if (text.isNullOrEmpty()) {
            reloadAdapterAfterSearch()
        } else {
            if (text.isNotEmpty()) {
                viewModel.searchCities(text)
            }
        }
        return true
    }

    private fun reloadAdapterAfterSearch() {
        viewModel.getRecentlyCities()
        viewModel.getNearCities()
        showRecyclerView()
    }

    private fun showRecyclerView() {
        binding.noResultsLayout.root.visibility = View.GONE
        binding.searchCityRecyclerView.visibility = View.VISIBLE
    }

    private fun showNoDataLayout() {
        binding.noResultsLayout.root.visibility = View.VISIBLE
        binding.searchCityRecyclerView.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        sectionAdapter = CitiesAdapter(viewModel.sectionedItemCallback)
        binding.searchCityRecyclerView.adapter = sectionAdapter
        binding.searchCityRecyclerView.setHasFixedSize(true)
    }

    override fun onDetach() {
        sectionAdapter = null
        binding.locationSearchView.setOnQueryTextListener(null)
        super.onDetach()
    }

    override fun onDestroyView() {
        searchEditText = null
        binding.unbind()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CitiesBottomFragment()

        const val TAG = "CitiesBottomFragment"
    }
}