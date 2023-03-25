package com.melitopolcherry.hackathon.features.eventdetails.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Parcelable
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import coil.imageLoader
import coil.request.ImageRequest
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.DetailsPerformerAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.DetailsOccurrencesCallback
import com.melitopolcherry.hackathon.adapters.callbacks.TicketCountCallback
import com.melitopolcherry.hackathon.adapters.callbacks.TicketsCallback
import com.melitopolcherry.hackathon.core.presentation.BaseDataBindingActivity
import com.melitopolcherry.hackathon.data.DateHelper.dateForHomeEvent
import com.melitopolcherry.hackathon.data.models.ProvidedIds
import com.melitopolcherry.hackathon.data.models.SimilarEventItem
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.data.models.event.ocurrences.OccurrencesItem
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.ActivityEventDetailsBinding
import com.melitopolcherry.hackathon.databinding.ItemBottomSheetBinding
import com.melitopolcherry.hackathon.features.bottom.DetailsMapBottomFragment
import com.melitopolcherry.hackathon.features.dialogs.GetStartedDialog
import com.melitopolcherry.hackathon.features.dialogs.TicketQuantityDialog
import com.melitopolcherry.hackathon.features.eventdetails.adapter.SimilarEventsAdapter
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.performer.LandingPagePerformerActivity
import com.melitopolcherry.hackathon.features.seatingchart.SeatingChartActivity
import com.melitopolcherry.hackathon.features.venue.LandingPageVenueActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil

@AndroidEntryPoint
class DetailsEventActivity : BaseDataBindingActivity<ActivityEventDetailsBinding>(),
    DetailsOccurrencesCallback,
    View.OnClickListener,
    TicketsCallback,
    TicketCountCallback,
    SimilarEventsAdapter.OnSimilarEventSelected {
    override val layoutResId = R.layout.activity_event_details
    val viewModel: DetailsEventViewModel by viewModels()

    private var youTubeP: YouTubePlayer? = null
    private var performersAdapter: DetailsPerformerAdapter? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayoutCompat>? = null

    var venueId: String? = null

    private val similarEventsAdapter: SimilarEventsAdapter by lazy {
        SimilarEventsAdapter(this)
    }

    override fun onCreate() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        WindowCompat.setDecorFitsSystemWindows(window, false)
        lifecycle.addObserver(binding.youTubePlayer)

        viewModel.isLoading.value = true
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val r = width / 16
        (binding.nestedContainer.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0,
            r * 9,
            0,
            0
        )
        binding.nestedContainer.setPadding(0, 0, 0, r * 9 + 8)

        val eventId = intent?.extras?.getString(Enums.BundleCodes.EventId.name)
        val ticketGroupId =
            intent?.extras?.get(Enums.BundleCodes.TicketGroupId.name) as? ProvidedIds

        if (eventId != null) {
            viewModel.getEventById(eventId, ticketGroupId)
        }

        viewModel.setTrackImage.observe(this) {
            setTrackImage(it)
        }
        viewModel.processError.observe(this) {
            processError(it) {}
        }
        viewModel.setUpEvent.observe(this) {
            setupEventToViews(it)
        }
        viewModel.setUpTicketGroup.observe(this) {
            setTickets(it)
        }
        viewModel.setVideo.observe(this) {
            setVideo(it)
        }
        viewModel.setSimilarEvents.observe(this) {
            setSimilarEvents(it)
        }

        binding.showAllTickets.setOnClickListener(this)
        binding.buttonSeeMore.setOnClickListener(this)
        binding.trackButton.setOnClickListener(this)
        binding.backButton.root.setOnClickListener(this)
        binding.eventLocationImage.setOnClickListener(this)
    }

    override fun onSimilarEventSelected(similarEvent: SimilarEventItem) {
        val intent = Intent(this, DetailsEventActivity::class.java)
        intent.putExtra(Enums.BundleCodes.EventId.name, similarEvent.id)
        startActivity(intent)
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

    @SuppressLint("SetTextI18n")
    private fun setupEventToViews(it: EventFull) {
        loadImageAndSet(it.imageUrl)

        binding.eventTitle.text = it.title
        binding.eventPlace.text = it.venue?.getShortPlace()

        if (it.eventDescription != null && it.eventDescription?.isNotEmpty()!!) {
            binding.eventDescription.text = it.eventDescription
            binding.groupDescription.isVisible = true
        } else binding.groupDescription.isVisible = false

        it.startDate?.let {
            binding.eventDate.text = dateForHomeEvent(it)
        }

        binding.eventAddress.text = it.venue?.getFullPlace()

        setupBottomSheet()

        if (it.isTracked != null) {
            setTrackImage(it.isTracked!!)
        }

        performersAdapter = DetailsPerformerAdapter { item -> onPerformerSelected(item) }

        val array = arrayListOf<PerformersItem>()

        it.performers?.let { list ->

            array.addAll(list as ArrayList)
            println("🙌🏻" + list.size)
            venueId = list.firstOrNull { it.subType == 1 }?.id
        }


        if (viewModel.event?.venue != null) {
            println("⚡️ viewModel.event?.venue?.imageUrl ${viewModel.event?.venue?.imageUrl}")
            val venuePerf =
                PerformersItem(
                    viewModel.event?.venue?.imageUrl,
                    viewModel.event?.venue?.id,
                    viewModel.event?.venue?.name,
                    viewModel.event?.venue?.name,
                    viewModel.event?.venue?.getFullPlace(),
                    viewModel.event?.venue,
                    1
                )

            array.add(venuePerf)
        }

        performersAdapter?.setData(array)

        binding.detailsPerformersRecycler.adapter = performersAdapter
        binding.detailsPerformersRecycler.setHasFixedSize(true)
    }

    private fun setSimilarEvents(events: List<SimilarEventItem>) = with(binding) {
        textSimilarEvents.isVisible = events.isNotEmpty()
        rvSimilarEvents.adapter = similarEventsAdapter
        similarEventsAdapter.setData(events)
    }

    private fun loadImageAndSet(url: String?) {
        val request = ImageRequest.Builder(this)
            .data(url)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .placeholder(R.drawable.placeholder_event)
            .target(
                onSuccess = { result ->
                    binding.collapsingImageView.setImageDrawable(result)
                    binding.layoutEventDetails.visibility = View.VISIBLE
                },
                onError = {
                    binding.layoutEventDetails.visibility = View.VISIBLE
                }
            )
            .build()
        imageLoader.enqueue(request)
    }

    private fun setTickets(list: List<TicketGroup>?) = with(binding) {
        showAllTickets.visibility = View.VISIBLE
        if (list != null && list.isNotEmpty()) {
            findMinMaxPriceAndSetToView(list)
            eventPrices.setTextColor(
                ContextCompat.getColor(
                    this@DetailsEventActivity,
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
                    this@DetailsEventActivity,
                    R.color.text_accent
                )
            )
            showAllTickets.text = getString(R.string.details_show_more_dates)
            showAllTickets.setOnClickListener {
                venueId?.let { id ->
                    val intent =
                        Intent(this@DetailsEventActivity, LandingPageVenueActivity::class.java)
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
        if (!blockButtons!!) {
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
                binding.backButton.root -> {
                    finish()
                }
                binding.eventLocationImage -> {
                    if (viewModel.event?.venue?.latitude != null && viewModel.event?.venue?.longitude != null) {
                        val mapBottomFragment =
                            DetailsMapBottomFragment(viewModel.event, viewModel.userLocation)
                        mapBottomFragment.show(supportFragmentManager, mapBottomFragment.tag)
                    } else {
                        Toast.makeText(this, "No coordinates", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.trackButton -> {
                    viewModel.event?.let { event ->
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
                                    val intent = Intent(this, LoginActivity::class.java)
                                    intent.putExtra(Enums.BundleCodes.LoginType.name, 111)
                                    startActivity(intent)
                                }
                            }.show(supportFragmentManager, "GetStartedDialog")
                        }
                    }
                }
            }
            runDelayedUnblock()
        }
    }

    private fun onShowAllTicketsClick() {
        if (viewModel.ticketGroups != null && viewModel.ticketGroups?.size!! > 0) {
            val intent = Intent(this@DetailsEventActivity, SeatingChartActivity::class.java)
            intent.putExtra(Enums.BundleCodes.Event.name, viewModel.event)
            intent.putParcelableArrayListExtra(
                Enums.BundleCodes.TicketGroups.name,
                viewModel.ticketGroups as ArrayList<out Parcelable>
            )
            startActivity(intent)
        } else {
            Toast.makeText(
                this@DetailsEventActivity, "Event doesn't have any tickets",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetDetailsTickets)
        bottomSheetBehavior?.isGestureInsetBottomIgnored = true
    }

    private fun setTrackImage(isTracked: Boolean) {
        viewModel.event?.isTracked = isTracked
        binding.trackButton.isSelected = isTracked
    }

    private fun onPerformerSelected(performer: PerformersItem) {
        if (!blockButtons!!) {
            blockButtons = true
            if (performer.subType != null) {
                val intent =
                    Intent(this@DetailsEventActivity, LandingPageVenueActivity::class.java)
                intent.putExtra(Enums.BundleCodes.LandingId.name, performer.id)
                startActivity(intent)
            } else {
                val intent =
                    Intent(this@DetailsEventActivity, LandingPagePerformerActivity::class.java)
                intent.putExtra(Enums.BundleCodes.LandingId.name, performer.id)
                startActivity(intent)
            }
            runDelayedUnblock()
        }
    }

    override val ticketsClickCallback = object : TicketsCallback.Callback {
        override fun onClick(ticket: TicketGroup?, view: ItemBottomSheetBinding) {
            if (!blockButtons!!) {
                blockButtons = true
                if (ticket?.splits?.size == 1) {

                } else {
                    TicketQuantityDialog(ticket?.splits!!, ticketsCountCallback).show(
                        supportFragmentManager,
                        TicketQuantityDialog.TAG
                    )
                }
                runDelayedUnblock()
            }
        }
    }

    override val ticketsCountCallback = object : TicketCountCallback.Callback {
        override fun onClick(count: Int) {
        }
    }

    override val occurrencesCallback = object : DetailsOccurrencesCallback.Callback {
        override fun onClick(occurrencesItem: OccurrencesItem) {
            if (!blockButtons!!) {
                blockButtons = true
                binding.eventDate.text = dateForHomeEvent(occurrencesItem.startDate!!)
                viewModel.getEventTicketGroups(occurrencesItem.providedIds!!)
                viewModel.event?.startDate = occurrencesItem.startDate
                viewModel.event?.venue?.city = occurrencesItem.city
                binding.eventPlace.text = viewModel.event?.venue?.getShortPlace()
                runDelayedUnblock()
            }
        }
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior != null && bottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            finish()
        }
    }
}