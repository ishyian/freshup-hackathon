package com.melitopolcherry.hackathon.features.venue

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.view.Display
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import coil.imageLoader
import coil.request.ImageRequest
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.LandingPageVenueAdapter
import com.melitopolcherry.hackathon.core.presentation.BaseDataBindingActivity
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.venue.VenueDetails
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.ActivityLandingPageVenueBinding
import com.melitopolcherry.hackathon.features.dialogs.GetStartedDialog
import com.melitopolcherry.hackathon.features.eventdetails.activity.DetailsEventActivity
import com.melitopolcherry.hackathon.features.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingPageVenueActivity : BaseDataBindingActivity<ActivityLandingPageVenueBinding>(),
    View.OnClickListener {
    override val layoutResId = R.layout.activity_landing_page_venue
    val viewModel: LandingPageVenueViewModel by viewModels()

    private var venueAdapter: LandingPageVenueAdapter? = null

    override fun onCreate() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val landingId = intent?.extras?.getString(Enums.BundleCodes.LandingId.name)
        viewModel.getVenueDetails(landingId)

        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val r = width / 16
        (binding.nestedContainer.layoutParams as? ViewGroup.MarginLayoutParams)?.updateMargins(
            top = r * 9 + 8
        )
        binding.emptyStateLayout.root.updatePadding(top = r * 9 + 8)

        viewModel.processError.observe(this) {
            processError(it) {}
        }
        viewModel.setTrackImage.observe(this) {
            binding.trackButton.isSelected = it
        }
        viewModel.setUpData.observe(this) {
            setupViews(it)
        }
        viewModel.setUpEvents.observe(this) {
            if (it != null && it.isNotEmpty()) {
                if (viewModel.page == 0) {
                    venueAdapter?.setData(it)
                } else {
                    venueAdapter?.updateData(it)
                }
                showContent()
            } else {
                showEmptyState()
            }
        }

        viewModel.layoutManager = LinearLayoutManager(this)
        venueAdapter = LandingPageVenueAdapter { item -> itemSelected(item) }
        binding.landingPageRecycler.layoutManager = viewModel.layoutManager
        binding.landingPageRecycler.adapter = venueAdapter
        binding.scrollView.setOnScrollChangeListener(viewModel.scrollListener)

        binding.trackButton.setOnClickListener(this)
        binding.backButton.root.setOnClickListener(this)
    }

    private fun setupViews(venue: VenueDetails?) {
        binding.venueName.text = venue?.name
        binding.venueAddress.text = getString(
            R.string.landing_page_address_template2,
            venue?.city?.buildingName,
            venue?.city?.name,
            venue?.city?.region
        )

        val request = ImageRequest.Builder(this)
            .data(if(venue?.imageUrl != null) venue.imageUrl else R.drawable.placeholder_venue)
            .placeholder(R.drawable.placeholder_venue)
            .error(R.drawable.placeholder_venue)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .target(
                onSuccess = { result ->
                    binding.venueImage.setImageDrawable(result)
                    viewModel.isLoading.postValue(false)
                    binding.venuePageLayout.visibility = View.VISIBLE
                },
                onError = {
                    viewModel.isLoading.postValue(false)
                    binding.venuePageLayout.visibility = View.VISIBLE
                }
            )
            .build()
        imageLoader.enqueue(request)

        venue?.isTracked?.let {
            binding.trackButton.isSelected = true
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.backButton.root -> {
                finish()
            }
            binding.trackButton -> {
                val tracked = viewModel.venueDetails?.isTracked
                val token = viewModel.authToken?.value?.accessToken
                if (token != null) {
                    if (tracked != null && tracked) {
                        viewModel.unTrackVenue(token)
                    } else {
                        viewModel.trackVenue(token)
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

    private fun showEmptyState() {
        binding.emptyStateLayout.root.visibility = View.VISIBLE
        binding.landingPageRecycler.visibility = View.INVISIBLE
    }

    private fun showContent() {
        binding.emptyStateLayout.root.visibility = View.GONE
        binding.landingPageRecycler.visibility = View.VISIBLE
    }

    private fun itemSelected(event: EventsItem) {
        println("🚀" + event.title)
        val intent = Intent(this, DetailsEventActivity::class.java)
        intent.putExtra(Enums.BundleCodes.EventId.name, event.id)
        intent.putExtra(Enums.BundleCodes.TicketGroupId.name, event.ticketEvolutionId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onBackPressed() {
        finish()
    }
}