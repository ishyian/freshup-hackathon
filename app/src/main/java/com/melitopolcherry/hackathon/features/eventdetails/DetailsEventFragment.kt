package com.melitopolcherry.hackathon.features.eventdetails

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import coil.imageLoader
import coil.request.ImageRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.DetailsPerformerAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.DetailsOccurrencesCallback
import com.melitopolcherry.hackathon.core.presentation.BaseDataBindingActivity
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.DateHelper.dateForHomeEvent
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.livedata.FiltersConfigLiveData
import com.melitopolcherry.hackathon.data.models.SimilarEventItem
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.data.models.event.ocurrences.OccurrencesItem
import com.melitopolcherry.hackathon.data.models.event.small.EventMap
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentDetailsEventBinding
import com.melitopolcherry.hackathon.features.bottom.DetailsMapBottomFragment
import com.melitopolcherry.hackathon.features.bottom.ShareBottomFragment
import com.melitopolcherry.hackathon.features.dialogs.GetStartedDialog
import com.melitopolcherry.hackathon.features.eventdetails.activity.DetailsEventActivity
import com.melitopolcherry.hackathon.features.eventdetails.adapter.SimilarEventsAdapter
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.features.performer.LandingPagePerformerActivity
import com.melitopolcherry.hackathon.features.seatingchart.SeatingChartActivity
import com.melitopolcherry.hackathon.features.venue.LandingPageVenueActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil

@AndroidEntryPoint
class DetailsEventFragment : BaseFragment<FragmentDetailsEventBinding>(),
    DetailsOccurrencesCallback, View.OnClickListener, Observer<EventsFilters>,
    SimilarEventsAdapter.OnSimilarEventSelected {

    private val viewModel: DetailsEventViewModel by viewModels()

    private var performersAdapter: DetailsPerformerAdapter? = null
    private val similarEventsAdapter: SimilarEventsAdapter by lazy {
        SimilarEventsAdapter(this)
    }

    var youTubeP: YouTubePlayer? = null

    private var blockButtons = false
    private var statusHeight = 0
    var selectedEvent: EventMap? = null
    var venueId: String? = null

    private var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>? = null
    private var filtersConfigLiveData: FiltersConfigLiveData? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDetailsEventBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.selectedEvent = selectedEvent

        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
        viewModel.setPerformerList.observe(this) {
            setPerformerList(it)
        }
        viewModel.setTrackImage.observe(this) {
            setTrackImage(it)
        }
        viewModel.setVideo.observe(this) {
            setVideo(it)
        }
        viewModel.setTickets.observe(this) {
            setTickets(it)
        }
        viewModel.setSimilarEvents.observe(this) {
            setSimilarEvents(it)
        }
        filtersConfigLiveData = FiltersConfigLiveData.instance
        filtersConfigLiveData?.getFilters()?.observe(this, this)
    }

    private fun setSimilarEvents(events: List<SimilarEventItem>) = with(binding) {
        textSimilarEvents.isVisible = events.isNotEmpty()
        rvSimilarEvents.adapter = similarEventsAdapter
        similarEventsAdapter.setData(events)
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        lifecycle.addObserver(binding.youTubePlayer)

        activity?.window?.decorView?.setOnApplyWindowInsetsListener { _, windowInsets ->
            statusHeight = windowInsets.systemWindowInsetTop
            binding.bottomContainer.updatePadding(bottom = windowInsets.systemWindowInsetBottom)
            return@setOnApplyWindowInsetsListener windowInsets
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutEventDetails)
        bottomSheetBehavior?.isHideable = true
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior?.addBottomSheetCallback(bottomSheetCallback)

        try {
            val display: Display? = activity?.windowManager?.defaultDisplay
            val size = Point()
            display?.getSize(size)
            val width: Int = size.x
            val r = width / 16
            (binding.nestedContainer.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(0, r * 9 + 8, 0, 0)
            binding.nestedContainer.setPadding(0, 0, 0, r * 9 + 8)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        binding.btnBack.setOnClickListener(this)
        binding.buttonSeeMore.setOnClickListener(this)
        binding.btnTrack.setOnClickListener(this)
        binding.eventLocationImage.setOnClickListener(this)
        binding.imageShare.setOnClickListener(this)

        selectedEvent?.let {
            showEventCard(it)
        }
    }

    override fun onChanged(eventFilters: EventsFilters?) {
        eventFilters?.let { t ->
            viewModel.searchSimilarEvents()
        }
    }

    fun showEventCard(event: EventMap) {
        binding.showAllTickets.visibility = View.INVISIBLE
        viewModel.selectedEvent = event

        loadImageAndSet(event)

        if (event.providedIds?.ticketnetwork != null || event.providedIds?.ticketevolution != null) {
            binding.showAllTickets.visibility = View.INVISIBLE
            viewModel.getEventTicketGroups(event.providedIds)
        }

        viewModel.getEventById(event.id!!, event.providedIds)

        if (event.isTracked != null) {
            setTrackImage(event.isTracked!!)
        }

        binding.textEventTitle.text = event.title

        event.startDate?.let {
            binding.textEventDate.text = dateForHomeEvent(it)
        }

        binding.textEventPlace.text = event.venue?.getShortPlace()
        binding.eventAddress.text = event.venue?.getFullPlace()

        if (event.eventDescription != null && event.eventDescription?.isNotEmpty()!!) {
            binding.eventDescription.text = event.eventDescription
            binding.groupDescription.isVisible = true
        } else binding.groupDescription.isVisible = false

        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private val bottomSheetCallback = object :
        BottomSheetBehavior.BottomSheetCallback() {
        @SuppressLint("ClickableViewAccessibility")
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> {
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    binding.contentScroll.scrollTo(0, 0)
                }
                else -> {}
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (slideOffset >= 0 && slideOffset < 1.0) {
                animateBottomSheet(slideOffset)
            }
        }
    }

    override fun onSimilarEventSelected(similarEvent: SimilarEventItem) {
        val intent = Intent(requireContext(), DetailsEventActivity::class.java)
        intent.putExtra(Enums.BundleCodes.EventId.name, similarEvent.id)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun loadImageAndSet(event: EventMap) {
        binding.imageSmall.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.placeholder_event
            )
        )
        binding.imageBig.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.placeholder_event
            )
        )
        val request = ImageRequest.Builder(requireContext())
            .data(event.imageUrl)
            .placeholder(R.drawable.placeholder_event)
            .error(R.drawable.placeholder_event)
            .target(
                onSuccess = { result ->
                    binding.imageSmall.setImageDrawable(result)
                    binding.imageBig.setImageDrawable(result)
                },
            )
            .build()
        requireContext().imageLoader.enqueue(request)
    }

    private fun setVideo(videoUrl: String?) {
        if (videoUrl == null) {
            lifecycle.removeObserver(binding.youTubePlayer)
            binding.videoCard.visibility = View.GONE
            return
        }
        binding.youTubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(
                @NonNull
                youTubePlayer: YouTubePlayer
            ) {
                youTubeP = youTubePlayer
                val videoId = videoUrl.removePrefix("https://www.youtube.com/watch?v=")
                youTubePlayer.loadVideo(videoId, 0f)
                youTubePlayer.pause()
            }
        })
    }

    private fun setPerformerList(list: ArrayList<PerformersItem>) {
        performersAdapter = DetailsPerformerAdapter { item -> onPerformerSelected(item) }

        performersAdapter?.setData(list)
        venueId = list.firstOrNull { it.subType == 1 }?.id

        binding.detailsPerformersRecycler.adapter = performersAdapter
        binding.detailsPerformersRecycler.setHasFixedSize(true)
    }

    private fun setTickets(list: List<TicketGroup>?) = with(binding) {
        showAllTickets.visibility = View.VISIBLE
        if (list != null && list.isNotEmpty()) {
            findMinMaxPriceAndSetToView(list)
            eventPrices.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_primary
                )
            )
            showAllTickets.text = getString(R.string.show_all_tickets_text)
            showAllTickets.setOnClickListener {
                onShowAllTicketsClick()
            }
        } else {
            eventPrices.text = getString(R.string.sold_out_text)
            eventPrices.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_accent
                )
            )
            showAllTickets.text = getString(R.string.details_show_more_dates)
            showAllTickets.setOnClickListener {
                venueId?.let { id ->
                    val intent =
                        Intent(requireContext(), LandingPageVenueActivity::class.java)
                    intent.putExtra(Enums.BundleCodes.LandingId.name, id)
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun findMinMaxPriceAndSetToView(it: List<TicketGroup>?) {
        val min = it?.minByOrNull { it.retailPrice!! }?.retailPrice
        val max = it?.maxByOrNull { it.retailPrice!! }?.retailPrice
        val roundedMin = ceil(min!!).toInt()
        val roundedMax = ceil(max!!).toInt()
        binding.eventPrices.text = "\$$roundedMin - \$$roundedMax"
    }

    override fun onClick(v: View?) {
        if (!blockButtons) {
            blockButtons = true
            when (v) {
                binding.buttonSeeMore -> {
                    if (binding.eventDescription.isExpanded) {
                        binding.buttonSeeMore.text = getString(R.string.more_button_text)
                        binding.eventDescription.collapse()
                    } else {
                        binding.buttonSeeMore.text = getString(R.string.show_less)
                        binding.eventDescription.expand()
                    }
                }
                binding.btnBack -> {

                }
                binding.eventLocationImage -> {
                    if (viewModel.fullEvent?.venue?.latitude != null && viewModel.fullEvent?.venue?.longitude != null) {
                        val mapBottomFragment =
                            DetailsMapBottomFragment(viewModel.fullEvent, viewModel.userLocation)
                        mapBottomFragment.show(childFragmentManager, mapBottomFragment.tag)
                    } else {
                        Toast.makeText(requireContext(), "Venue is null", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                binding.imageShare -> {
                    val shareBottomFragment =
                        ShareBottomFragment(viewModel.fullEvent)
                    shareBottomFragment.show(childFragmentManager, shareBottomFragment.tag)
                }
                binding.btnTrack -> {
                    viewModel.selectedEvent?.let { event ->
                        if (viewModel.authToken?.value?.accessToken != null) {
                            val tracked = event.isTracked
                            if (tracked != null && !tracked) {
                                viewModel.trackEvent()
                            } else {
                                viewModel.unTrackEvent()
                            }
                        } else {
                            GetStartedDialog {
                                if (it) {
                                    val intent = Intent(requireContext(), LoginActivity::class.java)
                                    intent.putExtra(
                                        Enums.BundleCodes.LoginType.name, 111
                                    )
                                    startActivity(intent)
                                }
                            }.show(childFragmentManager, "GetStartedDialog")
                        }
                    }
                }
            }
            runDelayedUnblock()
        }
    }

    private fun onShowAllTicketsClick() {
        if (viewModel.ticketGroups != null && viewModel.ticketGroups?.size!! > 0) {
            val intent = Intent(requireContext(), SeatingChartActivity::class.java)
            intent.putExtra(Enums.BundleCodes.Event.name, viewModel.fullEvent)
            intent.putParcelableArrayListExtra(
                Enums.BundleCodes.TicketGroups.name,
                viewModel.ticketGroups as ArrayList<out Parcelable>
            )
            startActivity(intent)
        } else {
            Toast.makeText(
                requireContext(), "Event doesn't have any tickets",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setTrackImage(isTracked: Boolean) {
        viewModel.selectedEvent?.isTracked = isTracked
        binding.btnTrack.isSelected = isTracked
    }

    private fun onPerformerSelected(performer: PerformersItem) {
        if (!blockButtons) {
            blockButtons = true
            if (performer.subType != null) {
                val intent =
                    Intent(requireContext(), LandingPageVenueActivity::class.java)
                intent.putExtra(Enums.BundleCodes.LandingId.name, performer.id)
                startActivity(intent)
            } else {
                val intent =
                    Intent(requireContext(), LandingPagePerformerActivity::class.java)
                intent.putExtra(Enums.BundleCodes.LandingId.name, performer.id)
                startActivity(intent)
            }
            runDelayedUnblock()
        }
    }

    override val occurrencesCallback = object : DetailsOccurrencesCallback.Callback {
        override fun onClick(occurrencesItem: OccurrencesItem) {
            println("🙂 ${occurrencesItem.providedIds}")
            if (!blockButtons) {
                blockButtons = true
                binding.textEventPlace.text = occurrencesItem.city?.name
                binding.textEventDate.text = dateForHomeEvent(occurrencesItem.startDate!!)
                binding.showAllTickets.visibility = View.INVISIBLE
                viewModel.getEventTicketGroups(occurrencesItem.providedIds)
                viewModel.selectedEvent?.startDate = occurrencesItem.startDate
                viewModel.selectedEvent?.venue?.city = occurrencesItem.city
                runDelayedUnblock()
            }
        }
    }

    fun hideEventCard() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        viewModel.selectedEvent = null
    }

    private fun animateBottomSheet(slideOffset: Float) {
        binding.imageSmall.alpha = 1 - slideOffset
        binding.viewBottomSheetDivider.alpha = 1 - slideOffset
        binding.btnBack.alpha = slideOffset

        if (slideOffset >= 0.05) {
            binding.imageConstraint.visibility = View.VISIBLE
            binding.imageSmall.visibility = View.GONE
            binding.bottomContainer.visibility = View.VISIBLE
        } else if (slideOffset <= 0.05) {
            binding.imageConstraint.visibility = View.GONE
            binding.imageSmall.visibility = View.VISIBLE
            binding.bottomContainer.visibility = View.GONE
        }
    }

    private fun changeViewWidth(view: View?, width: Int) {
        val layoutParams: ViewGroup.LayoutParams? = view?.layoutParams
        layoutParams?.height = width
        view?.layoutParams = layoutParams
    }

    fun runDelayedUnblock() {
        Handler(Looper.getMainLooper()).postDelayed({
                                                        blockButtons = false
                                                    }, BaseDataBindingActivity.DELAY_IN_MS)
    }

    override fun onDestroyView() {
        binding.imageSmall.background = null
        binding.imageBig.background = null

        binding.showAllTickets.setOnClickListener(null)
        binding.btnTrack.setOnClickListener(null)

        binding.eventLocationImage.setOnClickListener(null)
        bottomSheetBehavior?.removeBottomSheetCallback(bottomSheetCallback)
        activity?.window?.decorView?.setOnApplyWindowInsetsListener(null)

        bottomSheetBehavior = null
        performersAdapter = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(it: EventMap) = DetailsEventFragment().apply {
            selectedEvent = it
        }

        const val TAG = "DetailsEventFragment"
    }
}