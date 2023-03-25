package com.melitopolcherry.hackathon.features.performer

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
import com.melitopolcherry.hackathon.adapters.LandingPagePerformerAdapter
import com.melitopolcherry.hackathon.core.presentation.BaseDataBindingActivity
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.performer.PerformerDetails
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.ActivityLandingPagePerformerBinding
import com.melitopolcherry.hackathon.features.dialogs.GetStartedDialog
import com.melitopolcherry.hackathon.features.eventdetails.activity.DetailsEventActivity
import com.melitopolcherry.hackathon.features.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingPagePerformerActivity : BaseDataBindingActivity<ActivityLandingPagePerformerBinding>(),
    View.OnClickListener {
    override val layoutResId = R.layout.activity_landing_page_performer

    val viewModel: LandingPagePreformerViewModel by viewModels()
    private var performerAdapter: LandingPagePerformerAdapter? = null

    override fun onCreate() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.isLoading.postValue(true)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val landingId = intent?.extras?.getString(Enums.BundleCodes.LandingId.name)
        viewModel.getPerformer(landingId)

        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val r = width / 16
        (binding.nestedContainer.layoutParams as? ViewGroup.MarginLayoutParams)?.updateMargins(top = r * 9 + 8)
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
                    performerAdapter?.setData(it)
                } else {
                    performerAdapter?.updateData(it)
                }
                showContent()
            } else {
                showEmptyState()
            }
        }

        performerAdapter = LandingPagePerformerAdapter { item -> itemSelected(item) }
        viewModel.layoutManager = LinearLayoutManager(this)
        binding.landingPageRecycler.adapter = performerAdapter
        binding.landingPageRecycler.layoutManager = viewModel.layoutManager
        binding.scrollView.setOnScrollChangeListener(viewModel.scrollListener)

        binding.trackButton.setOnClickListener(this)
        binding.backButton.root.setOnClickListener(this)
    }

    private fun setupViews(performer: PerformerDetails?) {
        binding.performerName.text = performer?.name
        binding.description.text = performer?.type
        val request = ImageRequest.Builder(this)
            .data(if (performer?.imageUrl != null) performer.imageUrl else R.drawable.placeholder_performer)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .placeholder(R.drawable.placeholder_performer)
            .error(R.drawable.placeholder_performer)
            .target(
                onSuccess = { result ->
                    binding.performerImage.setImageDrawable(result)
                    viewModel.isLoading.postValue(false)
                    binding.landingPageLayout.visibility = View.VISIBLE
                },
                onError = {
                    viewModel.isLoading.postValue(false)
                    binding.landingPageLayout.visibility = View.VISIBLE
                }
            )
            .build()
        imageLoader.enqueue(request)

        performer?.isTracked?.let {
            binding.trackButton.isSelected = it
        }
    }

    private fun showEmptyState() {
        binding.landingPageRecycler.visibility = View.INVISIBLE
        binding.emptyStateLayout.root.visibility = View.VISIBLE
        binding.upcomingEvents.visibility = View.GONE
    }

    private fun showContent() {
        binding.emptyStateLayout.root.visibility = View.GONE
        binding.landingPageRecycler.visibility = View.VISIBLE
        binding.upcomingEvents.visibility = View.VISIBLE
    }

    private fun itemSelected(event: EventsItem) {
        val intent = Intent(this, DetailsEventActivity::class.java)
        intent.putExtra(Enums.BundleCodes.EventId.name, event.id)
        intent.putExtra(Enums.BundleCodes.TicketGroupId.name, event.ticketEvolutionId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.backButton.root -> {
                finish()
            }
            binding.trackButton -> {
                val tracked = viewModel.performerDetails?.isTracked
                val token = viewModel.authToken?.value?.accessToken
                if (token != null) {
                    if (tracked != null && tracked) {
                        viewModel.unTrackPerformer(token)
                    } else {
                        viewModel.trackPerformer(token)
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

    override fun onBackPressed() {
        finish()
    }
}