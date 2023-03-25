package com.melitopolcherry.hackathon.features.bottom

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.FiltersSeatTypeGridAdapter
import com.melitopolcherry.hackathon.adapters.QuantityTicketsAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.QuantityCallback
import com.melitopolcherry.hackathon.adapters.callbacks.StadiumFiltersCallback
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.data.models.TicketsFilter
import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.BottomSheetTicketFiltersBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TicketFiltersBottomFragment(
    private val ticketGroups: List<TicketGroup>,
    private var filter: TicketsFilter,
    private val stadiumFiltersCallback: StadiumFiltersCallback.Callback
) :
    BaseBottomFragmentDialog(), QuantityCallback,
    View.OnClickListener {

    private var filteredList: ArrayList<TicketGroup> = arrayListOf()
    private var minMaxPrices: Pair<Int, Int>? = null
    private var quantityTicketsAdapter: QuantityTicketsAdapter? = null
    private var adapterTicketType: FiltersSeatTypeGridAdapter? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    lateinit var binding: BottomSheetTicketFiltersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetTicketFiltersBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findMinMaxPriceAndSettoView()

        setupQuantityList()
        setupRangeBar()

        binding.closeFiltersButton.setOnClickListener(this)
        binding.resetFiltersButton.setOnClickListener(this)
        binding.applyFilterButton.setOnClickListener(this)

        adapterTicketType = FiltersSeatTypeGridAdapter { seat ->
            println("⚡️ adapterTicketType${seat}")
            if(!filter.selectedSeatTypes.contains(seat)){
                filter.selectedSeatTypes.add(seat)
            }else{
                filter.selectedSeatTypes.remove(seat)
            }
            filterTickets()//
        }
        adapterTicketType?.setData(
            filter.selectedSeatTypes
        )
        binding.applyFilterButton.text = "View ${ticketGroups.size} Lists"

        binding.gridViewSeatType.adapter = adapterTicketType
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.bottom_sheet_ticket_filters, null)
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

    override fun onClick(v: View?) {
        when (v) {
            binding.closeFiltersButton -> {
                dismiss()
            }
            binding.resetFiltersButton -> {
                filter = TicketsFilter()
                setupQuantityList()
                binding.priceRangeBar.selectedMinMaxValue = Pair(0.0, 1.0)
                filterTickets()
                adapterTicketType?.setData(
                    filter.selectedSeatTypes
                )
                binding.applyFilterButton.text = "View ${ticketGroups.size} Lists"
            }
            binding.applyFilterButton -> {
                filterTickets()
                stadiumFiltersCallback.onFilter(filteredList, filter)
                dismiss()
            }
        }
    }

    private fun setupQuantityList() {
        quantityTicketsAdapter = QuantityTicketsAdapter(filter.selectedQuantities, quantityCallback)
        binding.quantityRecycler.adapter = quantityTicketsAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun setupRangeBar() {

        val min = minMaxPrices?.first
        val max = minMaxPrices?.second
        val difference = min?.let { max?.minus(it) }

        val minPosition = filter.minRangePosition
        val maxPosition = filter.maxRangePosition

        if (minPosition != null && maxPosition != null) {
            binding.priceRangeBar.selectedMinMaxValue = Pair(minPosition, maxPosition)

            binding.priceRangeBar.invalidate()
        } else {
            filter.minPriceRange = minMaxPrices?.first
            filter.maxPriceRange = minMaxPrices?.second
        }

        binding.priceRangeBar.thumbTextGenerator = { value ->
            "$${min?.plus((value * difference!!))?.toInt()}"
        }
        binding.priceRangeBar.rangeSeekBarChangeListener = { minValue, maxValue ->
            filter.minRangePosition = minValue
            filter.maxRangePosition = maxValue

            filter.minPriceRange = min?.plus((minValue * difference!!))?.toInt()
            filter.maxPriceRange = min?.plus((maxValue * difference!!))?.toInt()

            filterTickets()//
        }
    }

    private fun findMinMaxPriceAndSettoView() {
        val min = ticketGroups.minByOrNull { it.retailPrice!! }?.retailPrice
        val max = ticketGroups.maxByOrNull { it.retailPrice!! }?.retailPrice

        minMaxPrices = Pair(min?.toInt()!!, max?.toInt()!!)
    }

    private fun filterTickets() {
        println("😡 filterTickets $filter")
        println("😡 filterTickets lll ${ticketGroups.size}")
        filteredList = arrayListOf()
        filteredList.addAll(ticketGroups)

        filteredList = filterByQuantity(filteredList)

        filteredList = filterByPrice(filteredList)

        filteredList = filterByTypes(filteredList)

        binding.applyFilterButton.text = "View ${filteredList.size} Lists"
    }

    private fun filterByQuantity(list: ArrayList<TicketGroup>): ArrayList<TicketGroup> {
        println("😡 filterByQuantity b${list.size}")

        val listBefore = arrayListOf<TicketGroup>()
        listBefore.addAll(list)
        if (filter.selectedQuantities.isNotEmpty() && list.isNotEmpty()) {
            for (i in 0 until list.size) {
                filter.selectedQuantities.forEach { quantity ->
                    if (!list[i].splits?.contains(quantity)!!) {
                        listBefore.remove(list[i] as TicketGroup?)
                    }
                }
            }
        }
        println("😡 filterByQuantity a${listBefore.size}")

        return listBefore
    }

    private fun filterByPrice(list: ArrayList<TicketGroup>): ArrayList<TicketGroup> {
        println("😡 filterByPrice b${list.size}")

        val listBefore = arrayListOf<TicketGroup>()
        listBefore.addAll(list)
        val min = filter.minPriceRange?.toDouble()
        val max = filter.maxPriceRange?.toDouble()
        if (min != null && max != null) {
            for (i in 0 until list.size) {
                if (list[i].retailPrice!! !in min..max) {
                    listBefore.remove(list[i] as TicketGroup?)
                }
            }
        }
        println("😡 filterByPrice a${listBefore.size}")

        return listBefore
    }

    private fun filterByTypes(list: ArrayList<TicketGroup>): ArrayList<TicketGroup> {
        println("😡 filterByTypes b${list.size}")

        val listBefore = arrayListOf<TicketGroup>()
        listBefore.addAll(list)
        if(filter.selectedSeatTypes.isNotEmpty()){
            for (i in 0 until list.size) {
                if (!filter.selectedSeatTypes.contains(Enums.TicketType.getTypeBy(list[i].type!!))) {
                    listBefore.remove(list[i] as TicketGroup?)
                }
            }
        }

        println("😡 filterByTypes a${listBefore.size}")

        return listBefore
    }

    override val quantityCallback = object : QuantityCallback.Callback {
        override fun onClick(quantities: ArrayList<Int>) {
            filter.selectedQuantities = quantities
            filterTickets()//
        }
    }

    companion object {
        const val TAG = "StadiumFilterBottomFragment"
    }
}