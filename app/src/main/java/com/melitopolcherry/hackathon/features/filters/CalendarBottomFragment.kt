package com.melitopolcherry.hackathon.features.filters

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.livedata.FiltersConfigLiveData
import com.melitopolcherry.hackathon.databinding.BottomSheetCalendarBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener
import java.util.Calendar
import java.util.Date

class CalendarBottomFragment : BaseBottomFragmentDialog(),
    OnRangeSelectedListener, View.OnClickListener, OnDateSelectedListener {

    lateinit var binding: BottomSheetCalendarBinding

    private var presentDay: Date? = null
    private var tomorrow: Calendar? = null
    private var saturday: Calendar? = null
    private var sunday: Calendar? = null

    private var selectedButton = 0

    private var filtersConfigLiveData: FiltersConfigLiveData? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCalendarBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filtersConfigLiveData = FiltersConfigLiveData.instance
        setupDates()

        binding.timeCalendar.setOnDateChangedListener(this)
        binding.timeCalendar.setOnRangeSelectedListener(this)
        binding.timeCalendar.rightArrow.setTint(ContextCompat.getColor(requireContext(), R.color.text_primary))
        binding.timeCalendar.leftArrow.setTint(ContextCompat.getColor(requireContext(), R.color.text_primary))

        binding.timeCalendar.topbarVisible = true
        binding.timeCalendar.invalidateDecorators()

        binding.applyButton.setOnClickListener(this)
        binding.todayText.setOnClickListener(this)
        binding.tomorrowText.setOnClickListener(this)
        binding.weekendText.setOnClickListener(this)
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_calendar, null)
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
            binding.applyButton -> {
                val eventsFiltersConfig: EventsFilters? =
                    if (filtersConfigLiveData?.getFilters()?.value == null) {
                        EventsFilters()
                    } else {
                        filtersConfigLiveData?.getFilters()?.value
                    }
                if (binding.timeCalendar.selectedDates.isNotEmpty()) {
                    eventsFiltersConfig?.dates = binding.timeCalendar.selectedDates
                } else {
                    eventsFiltersConfig?.dates = listOf(CalendarDay.today())
                }
                eventsFiltersConfig?.page = 0
                filtersConfigLiveData?.getFilters()?.value = eventsFiltersConfig
                dismiss()
            }
            binding.todayText -> {
                selectedButton = 0
                makeButtonUnselected(binding.weekendText, binding.weekend)
                makeButtonUnselected(binding.tomorrowText, binding.tomorrow)
                makeButtonSelected(binding.todayText, binding.today)
                binding.timeCalendar.clearSelection()
                binding.timeCalendar.setDateSelected(CalendarDay.today(), true)
            }
            binding.tomorrowText -> {
                selectedButton = 1
                makeButtonUnselected(binding.weekendText, binding.weekend)
                makeButtonSelected(binding.tomorrowText, binding.tomorrow)
                makeButtonUnselected(binding.todayText, binding.today)
                binding.timeCalendar.clearSelection()
                binding.timeCalendar.setDateSelected(
                    calendarDayFromDate(tomorrow), true
                )
            }
            binding.weekendText -> {
                selectedButton = 2
                makeButtonSelected(binding.weekendText, binding.weekend)
                makeButtonUnselected(binding.tomorrowText, binding.tomorrow)
                makeButtonUnselected(binding.todayText, binding.today)
                binding.timeCalendar.clearSelection()
                binding.timeCalendar.setDateSelected(calendarDayFromDate(saturday), true)
                binding.timeCalendar.setDateSelected(calendarDayFromDate(sunday), true)
            }
        }
    }

    private fun makeButtonSelected(text: AppCompatTextView, button: FrameLayout) {
        button.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.quantity_background_selected)
        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun makeButtonUnselected(text: AppCompatTextView, button: FrameLayout) {
        button.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.quantity_background)
        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupDates() {
        val filters = filtersConfigLiveData?.getFilters()?.value
        binding.timeCalendar.clearSelection()

        presentDay = DateHelper.getStartOfDay(Calendar.getInstance().time)

        tomorrow = Calendar.getInstance()
        tomorrow?.add(Calendar.DATE, 1)

        saturday = Calendar.getInstance()
        saturday?.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)

        sunday = Calendar.getInstance()
        sunday?.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        sunday?.set(Calendar.DATE, sunday?.get(Calendar.DATE)!! + 1)

        if (saturday?.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance()
                .getActualMaximum(Calendar.DAY_OF_MONTH)
        ) {
            sunday?.set(Calendar.DAY_OF_MONTH, 1)
            sunday?.set(Calendar.MONTH, sunday?.get(Calendar.MONTH)!! + 1)
        }

        if (filters?.dates?.size == 0) {
            makeButtonSelected(binding.todayText, binding.today)
            binding.timeCalendar.setDateSelected(CalendarDay.today(), true)
        } else if (filters?.dates?.size == 1) {
            when (filters.dates[0]) {
                CalendarDay.today() -> {
                    makeButtonSelected(binding.todayText, binding.today)
                    binding.timeCalendar.setDateSelected(filters.dates[0], true)
                }
                calendarDayFromDate(tomorrow) -> {
                    makeButtonSelected(binding.tomorrowText, binding.tomorrow)
                }
                else -> {
                    binding.timeCalendar.setDateSelected(filters.dates[0], true)
                }
            }
        } else if (filters?.dates?.size == 2 && (filters.dates[0] == calendarDayFromDate(saturday) && filters.dates[1] == calendarDayFromDate(
                sunday
            ) || filters.dates[1] == calendarDayFromDate(sunday) && filters.dates[0] == calendarDayFromDate(
                saturday
            ))
        ) {
            makeButtonSelected(binding.weekendText, binding.weekend)
        } else {
            filters?.dates?.forEach {
                binding.timeCalendar.setDateSelected(it, true)
            }
        }
    }

    override fun onDateSelected(
        widget: MaterialCalendarView, date: CalendarDay,
        selected: Boolean
    ) {
        widget.invalidateDecorators()
    }

    override fun onRangeSelected(widget: MaterialCalendarView, dates: List<CalendarDay>) {
        widget.invalidateDecorators()
    }

    private fun calendarDayFromDate(date: Calendar?): CalendarDay {
        return CalendarDay.from(
            date?.get(Calendar.YEAR)!!,
            date.get(Calendar.MONTH) + 1,
            date.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onDetach() {
        presentDay = null
        tomorrow = null
        saturday = null
        sunday = null
        binding.applyButton.setOnClickListener(null)
        super.onDetach()
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarBottomFragment()

        const val TAG = "CalendarBottomFragment"
    }
}