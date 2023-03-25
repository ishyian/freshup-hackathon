package com.melitopolcherry.hackathon.features.bottom

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.ChangeAddressAutocompleteAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.ChangeAddressCallback
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.data.BuildConfig
import com.melitopolcherry.hackathon.data.models.Address
import com.melitopolcherry.hackathon.databinding.BottomSheetChangeAddressBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeAddressBottomFragment(private val callback: ChangeAddressCallback.Callback) :
    BaseBottomFragmentDialog(), View.OnClickListener,
    ChangeAddressAutocompleteAdapter.ClickListener, SearchView.OnQueryTextListener {

    lateinit var binding: BottomSheetChangeAddressBinding

    override fun getTheme(): Int = R.style.AddressBottomSheetDialog

    private var autoCompleteAdapter: ChangeAddressAutocompleteAdapter? = null
    private var searchEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetChangeAddressBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Places.initialize(requireContext(), BuildConfig.PLACES_API_KEY)

        autoCompleteAdapter = ChangeAddressAutocompleteAdapter(requireContext())
        autoCompleteAdapter?.setClickListener(this)

        binding.addressRecyclerView.adapter = autoCompleteAdapter
        autoCompleteAdapter?.notifyDataSetChanged()

        searchEditText =
            binding.searchPlaces.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText?.textSize =
            resources.getDimension(R.dimen.search_text_size) / resources.displayMetrics.scaledDensity

        searchEditText?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.searchPlaces.setOnQueryTextListener(this)
        binding.backButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.backButton -> {
                dismiss()
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_change_address, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.skipCollapsed = true
            dialog.setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.searchPlaces.requestFocus()
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.toString().isNotEmpty()) {
            autoCompleteAdapter?.filter?.filter(newText.toString())
            if (binding.addressRecyclerView.visibility == View.INVISIBLE) {
                binding.addressRecyclerView.visibility = View.VISIBLE
            }
        } else {
            if (binding.addressRecyclerView.visibility == View.VISIBLE) {
                binding.addressRecyclerView.visibility = View.INVISIBLE
            }
        }
        return true
    }

    override fun onPlaceSelected(place: Place) {
        val addressList = place.addressComponents?.asList()
        addressList?.let { addressComponents ->
            if (addressComponents.size <= 6) {
                Toast.makeText(requireContext(), "We need more info", Toast.LENGTH_SHORT).show()
                return@let
            }
            val address = Address()
            address.country = addressComponents.firstOrNull { it.types.contains("country") }?.shortName ?: ""
            address.postalCode = addressComponents.firstOrNull { it.types.contains("postal_code") }?.shortName ?: ""
            address.region =
                addressComponents.firstOrNull { it.types.contains("administrative_area_level_1") }?.shortName ?: ""
            val streetNumber = addressComponents.firstOrNull { it.types.contains("street_number") }?.shortName ?: ""
            val route = addressComponents.firstOrNull { it.types.contains("route") }?.shortName ?: ""
            address.streetAddress = "$streetNumber $route"
            address.city =
                addressComponents.firstOrNull { it.types.contains("locality") || it.types.contains("sublocality") }?.shortName
                    ?: ""
            println("🤧 final ADDRESS $address")
            callback.onChanged(address)
            dismiss()
        }
    }

    override fun onDestroy() {
        binding.addressRecyclerView.adapter = null
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(changeAddressCallback: ChangeAddressCallback.Callback) =
            ChangeAddressBottomFragment(changeAddressCallback)

        const val TAG = "ChangeAddressBottomFragment"
    }
}