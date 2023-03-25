package com.melitopolcherry.hackathon.features.seatingchart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.TicketGroupAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.ItemTicketGroupCallback
import com.melitopolcherry.hackathon.adapters.callbacks.TicketCountCallback
import com.melitopolcherry.hackathon.core.presentation.BaseDataBindingActivity
import com.melitopolcherry.hackathon.data.DateHelper.shortDate
import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.ActivitySeatingChartBinding
import com.melitopolcherry.hackathon.databinding.ItemTicketGroupBinding
import com.melitopolcherry.hackathon.features.bottom.TicketFiltersBottomFragment
import com.melitopolcherry.hackathon.features.dialogs.PicturePreviewDialog
import com.melitopolcherry.hackathon.features.dialogs.TicketQuantityDialog
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SeatingChartActivity : BaseDataBindingActivity<ActivitySeatingChartBinding>(), View.OnClickListener,
    ItemTicketGroupCallback, TicketCountCallback {
    override val layoutResId = R.layout.activity_seating_chart
    val viewModel: SeatingChartViewModel by viewModels()

    private var layoutManager: LinearLayoutManager? = null
    private var ticketGroupAdapter: TicketGroupAdapter? = null

    override fun onCreate() = with(binding) {
        lifecycleOwner = this@SeatingChartActivity
        WindowCompat.setDecorFitsSystemWindows(window, false)
        loadingView.visibility = View.VISIBLE

        viewModel.event = intent?.extras?.get(Enums.BundleCodes.Event.name) as EventFull?
        viewModel.ticketGroups =
            intent?.extras?.getParcelableArrayList(Enums.BundleCodes.TicketGroups.name)
                ?: arrayListOf()

        viewModel.event?.let {
            ticketsCount.text =
                String.format(
                    getString(R.string.tickets_count_template),
                    viewModel.ticketGroups.size
                )
            venueName.text = String.format(
                getString(
                    R.string.stadium_venue_name_template
                ),
                it.venue?.name,
                shortDate(it.startDate!!)
            )
            ticketNameAndDate.text = it.title
        }

        initObservers()
        loadWebStadium()
        setupClickListener(filtersButton, resetButton, backButton.root, seatViewButton)
        initTicketsList()

        viewModel.initial()
    }

    private fun initTicketsList() = with(binding) {
        ticketsRecycler.post {
            ticketGroupAdapter?.recyclerHeight = ticketsRecycler.height
        }
        ticketGroupAdapter = TicketGroupAdapter()
        ticketGroupAdapter?.setCallback(ticketsClickCallback)
        layoutManager = LinearLayoutManager(this@SeatingChartActivity)
        ticketsRecycler.setHasFixedSize(true)
        ticketsRecycler.layoutManager = layoutManager
        ticketsRecycler.adapter = ticketGroupAdapter
        ticketsRecycler.addOnScrollListener(scrollListener)
    }

    private fun initObservers() = with(viewModel) {
        observe(isLoading) {
            binding.loadingView.visibility = if (it) View.VISIBLE else View.GONE
        }
        observe(showSeatViewButton) {
            binding.seatViewButton.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        observe(removeHighlight) {
            if (it) {
                removeHighlight()
            }
        }
        observe(setFilteredData) { filteredList ->
            if (filteredList.isNotEmpty()) {
                if (binding.emptyState.visibility == View.VISIBLE) {
                    binding.emptyState.visibility = View.GONE
                }
                viewModel.selectedTicketGroups = arrayListOf()
                viewModel.selectedTicketGroups.addAll(filteredList)
                if (viewModel.stadiumState == Enums.StadiumState.HAS_STADIUM) {
                    binding.stadiumWebView.evaluateJavascript(
                        "javascript:Seatics.MapComponent.addTicketData([${
                            viewModel.setTicketsInStadium(filteredList)
                        }])"
                    ) {}
                    ticketGroupAdapter?.setData(filteredList, viewModel.sectorColorsMap)
                } else {
                    ticketGroupAdapter?.setData(filteredList, hashMapOf())
                }
            } else {
                showEmptyState()
                removeHighlight()
            }
        }
        observe(highlightTicket) {
            highlightTicket(it)
        }
        observe(setData) {
            if (it.isNotEmpty()) {
                if (binding.emptyState.visibility == View.VISIBLE) {
                    binding.emptyState.visibility = View.GONE
                }
                ticketGroupAdapter?.setData(it, viewModel.sectorColorsMap)
            } else {
                showEmptyState()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun loadWebStadium() {
        //        val width = resources.getDimension(R.dimen.sc_width)
        //        val height = resources.getDimension(R.dimen.sc_height)
        val width = 360
        val height = 290

        println("\n\n🕰 \n $width  |||  $height \n\n 🕰")
        binding.stadiumWebView.webChromeClient = WebChromeClient()
        binding.stadiumWebView.settings.setSupportZoom(true)
        binding.stadiumWebView.settings.javaScriptEnabled = true
        binding.stadiumWebView.settings.loadWithOverviewMode = true
        binding.stadiumWebView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        binding.stadiumWebView.settings.useWideViewPort = true
        binding.stadiumWebView.settings.builtInZoomControls = true
        binding.stadiumWebView.settings.displayZoomControls = false
        binding.stadiumWebView.loadUrl("file:///android_res/raw/index.html")

        val backColor = ContextCompat.getColor(this, R.color.seating_chart_background)
        val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and backColor)

        binding.stadiumWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.evaluateJavascript(viewModel.loadMap(width, height, hexColor)) {}
            }
        }
        binding.stadiumWebView.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun mapLoaded() {
                println("🤖 MAPLOADED!!!")
                viewModel.stadiumState = Enums.StadiumState.HAS_STADIUM
                runOnUiThread {
                    binding.loadingView.visibility = View.GONE
                    binding.stadiumWebLayout.visibility = View.VISIBLE
                    binding.resetButton.visibility = View.VISIBLE
                }
                Timber.d("Seating chart loading success")
            }
        }, "MapLoaded")
        binding.stadiumWebView.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun mapFailed() {
                viewModel.stadiumState = Enums.StadiumState.NO_STADIUM
                runOnUiThread {
                    binding.loadingView.visibility = View.GONE
                    binding.stadiumWebLayout.visibility = View.GONE
                    binding.stadiumPlaceholder.visibility = View.VISIBLE
                    ticketGroupAdapter?.setData(viewModel.ticketGroups, viewModel.sectorColorsMap)
                }
                println("🤖 js FAILED")
                Timber.d("Seating chart loading failed")
            }
        }, "MapFailed")
        binding.stadiumWebView.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun selectTickets(input: Array<String>?) {
                input?.forEach {
                    println("🤖 js Selected sec $it")
                }
                if (viewModel.setColors) {
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            binding.loadingView.visibility = View.GONE
                            viewModel.getTicketsForSection(input)
                        }, 800
                    )
                } else {
                    viewModel.getTicketsForSection(input)
                }
            }
        }, "Android")
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.filtersButton -> {
                val isAdded =
                    supportFragmentManager.findFragmentByTag(TicketFiltersBottomFragment.TAG)?.isAdded
                if (isAdded == null || !isAdded) {
                    if (viewModel.ticketGroups.isNotEmpty()) {
                        TicketFiltersBottomFragment(
                            viewModel.ticketGroups,
                            viewModel.ticketsFilter,
                            viewModel.stadiumFiltersCallback
                        ).show(supportFragmentManager, TicketFiltersBottomFragment.TAG)
                    }
                }
            }
            binding.backButton.root -> {
                onBackPressed()
            }
            binding.resetButton -> {
                resetStadium()
            }
            binding.seatViewButton -> {
                if (viewModel.selectedTicketUrl != null && viewModel.selectedTicketUrl?.length!! > 5) {
                    PicturePreviewDialog(
                        viewModel.selectedTicketUrl!!
                    ).show(
                        supportFragmentManager,
                        "PicturePreviewDialog"
                    )
                }
            }
        }
    }

    private fun removeHighlight() {
        ticketGroupAdapter?.setData(arrayListOf(), hashMapOf())
        binding.stadiumWebView.evaluateJavascript(
            "javascript:Seatics.MapComponent.addTicketData([])"
        ) {}
        binding.stadiumWebView.evaluateJavascript(
            "javascript:Seatics.MapComponent.highlightTicket(null)"
        ) {}
        binding.stadiumWebView.evaluateJavascript(
            "javascript:Seatics.Tooltip.showTooltip(null, null, null, null, null, !1)"
        ) {}
    }

    private fun showEmptyState() {
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun highlightTicket(ticketGroup: TicketGroup?) {
        binding.ticketsCount.text =
            getString(R.string.section_row_template, ticketGroup?.section, ticketGroup?.row)
        binding.stadiumWebView.evaluateJavascript(
            "javascript:Seatics.MapComponent.highlightTicket(ticketGroups['${ticketGroup?.id}'])"
        ) {}
        binding.stadiumWebView.evaluateJavascript(
            "javascript:Seatics.Tooltip.showTooltip(ticketGroups['${ticketGroup?.id}'].section, 1, 1, null, 1, !1)"
        ) {}
    }

    private fun resetStadium() {
        if (viewModel.ticketGroups.size == viewModel.selectedTicketGroups.size) {
            binding.ticketsRecycler.scrollToPosition(0)
        } else {
            binding.stadiumWebView.evaluateJavascript(
                "javascript:resetTickets()"
            ) {}
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            try {
                val visibleItemPosition = layoutManager?.findFirstCompletelyVisibleItemPosition()
                if (visibleItemPosition != null && visibleItemPosition >= 0) {
                    val ticket = if (viewModel.stadiumState == Enums.StadiumState.HAS_STADIUM) {
                        viewModel.selectedTicketGroups[visibleItemPosition]
                    } else {
                        viewModel.ticketGroups[visibleItemPosition]
                    }
                    val selectedView =
                        recyclerView.findViewHolderForAdapterPosition(visibleItemPosition)
                    ticketGroupAdapter?.highlight((selectedView as? TicketGroupAdapter.ViewHolder)?.binding!!)
                    viewModel.setSelectedTicket(ticket)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override val ticketsCountCallback = object : TicketCountCallback.Callback {
        override fun onClick(count: Int) {

        }
    }

    override val ticketsClickCallback = object : ItemTicketGroupCallback.Callback {
        override fun onClick(ticket: TicketGroup?, view: ItemTicketGroupBinding) {
            viewModel.lastScrollSelectedTicket = ticket

            if (ticket?.splits?.size == 1) {

            } else {
                TicketQuantityDialog(ticket?.splits!!, ticketsCountCallback).show(
                    supportFragmentManager,
                    TicketQuantityDialog.TAG
                )
            }
        }
    }
}