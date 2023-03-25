package com.melitopolcherry.hackathon.features.orderdetails

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PorterDuff
import android.net.Uri
import android.print.PrintManager
import android.provider.CalendarContract
import android.util.Base64
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.CloudPrintDocumentAdapter
import com.melitopolcherry.hackathon.adapters.WeatherAdapter
import com.melitopolcherry.hackathon.core.presentation.BaseDataBindingActivity
import com.melitopolcherry.hackathon.custom_view.setSafeOnClickListener
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.models.order.OrderDetails
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.ActivityOrderDetailsBinding
import com.melitopolcherry.hackathon.features.bottom.OrderLinksBottomFragment
import com.melitopolcherry.hackathon.features.dialogs.PdfPreviewDialog
import com.melitopolcherry.hackathon.features.venue.LandingPageVenueActivity
import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Polygon
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.scalebar.scalebar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

@AndroidEntryPoint
class DetailsOrderActivity : BaseDataBindingActivity<ActivityOrderDetailsBinding>(), View.OnClickListener,
        (View) -> Unit {
    override val layoutResId = R.layout.activity_order_details
    val viewModel: DetailsOrderViewModel by viewModels()

    private var weatherAdapter: WeatherAdapter? = null
    private var mapboxMap: MapboxMap? = null

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun onCreate() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel.processError.observe(this) {
            processError(it) {}
        }
        viewModel.setUpViews.observe(this) {
            binding.collapsingImageView.load(it.image)
            binding.orderDetailsMain.visibility = View.VISIBLE
            loadImageAndSet(it?.image)
            setupViews(it, viewModel.userLocation, viewModel.eventLocation)
        }
        viewModel.setWeather.observe(this) {
            val currentTemp = it.list?.get(0)?.main?.temp?.toInt()
            binding.currentWeatherTemp.text = getString(R.string.temp_template, currentTemp)
            val selectedImageName = "ic_${it?.list?.get(0)?.weather?.get(0)?.icon}"
            println("⛵️Main|||||$selectedImageName")
            val resourceId = resources.getIdentifier(selectedImageName, "drawable", packageName)

            if (resourceId != 0) {
                binding.currentWeatherImg.setImageResource(resourceId)
            }
            val currentDesc = it?.list?.get(0)?.weather?.get(0)?.description
            binding.currentWeatherImg.setColorFilter(
                ContextCompat.getColor(this, android.R.color.holo_orange_light),
                PorterDuff.Mode.SRC_IN
            )
            binding.weatherDescription.text = currentDesc
            it?.list?.let { itList ->
                weatherAdapter?.setData(itList.subList(1, 5))
            }
        }

        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val r = width / 16
        val layoutParams = (binding.scrollLinear.layoutParams as? ViewGroup.MarginLayoutParams)
        layoutParams?.setMargins(0, r * 9 + 8, 0, 0)
        binding.scrollLinear.layoutParams = layoutParams
        binding.scrollLinear.setPadding(0, 0, 0, r * 9 + 8)

        weatherAdapter = WeatherAdapter()
        binding.weatherRecycler.adapter = weatherAdapter

        val orderId = intent.extras?.getString(Enums.BundleCodes.OrderId.name)
        viewModel.eventLocation = intent.extras?.get(Enums.BundleCodes.Point.name) as LatLng

        orderId?.let {
            viewModel.getOrderDetails(orderId)
        }
        mapboxMap = binding.mapView.getMapboxMap()
        binding.mapView.gestures.pitchEnabled = false
        binding.mapView.gestures.scrollEnabled = false
        binding.mapView.gestures.rotateEnabled = false
        binding.mapView.gestures.doubleTapToZoomInEnabled = false
        binding.mapView.scalebar.enabled = false

        binding.backButton.root.setOnClickListener(this)
        binding.printButton.setOnClickListener(this)
        binding.moreEventsButton.setOnClickListener(this)
        binding.addCalendarButton.setOnClickListener(this)
        binding.showTicketsButton.setOnClickListener(this)
        binding.navigateButton.setSafeOnClickListener(this)
        binding.supportButton.setSafeOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.backButton.root -> {
                finish()
            }
            binding.moreEventsButton -> {
                val intent = Intent(this, LandingPageVenueActivity::class.java)
                intent.putExtra(Enums.BundleCodes.LandingId.name, viewModel.order?.venue?.id)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            binding.printButton -> {
                if (viewModel.ticket?.size != null && viewModel.ticket?.size!! > 0) {
                    doPhotoPrint(viewModel.ticket?.get(0)!!, viewModel.order)
                } else {
                    Toast.makeText(
                        this@DetailsOrderActivity,
                        "Your ticket currently is unavailable",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            binding.addCalendarButton -> {
                viewModel.order?.time?.let {
                    val calendarIntent = Intent(Intent.ACTION_EDIT)
                    calendarIntent.type = "vnd.android.cursor.item/event"
                    calendarIntent.putExtra(CalendarContract.Events.TITLE, viewModel.order?.name)
                    calendarIntent.putExtra(
                        CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        DateHelper.dateToLong(it)
                    )
                    calendarIntent.putExtra(
                        CalendarContract.EXTRA_EVENT_END_TIME,
                        DateHelper.dateToLong(it)
                    )
                    calendarIntent.putExtra(
                        CalendarContract.Events.EVENT_LOCATION,
                        viewModel.order?.venue?.city?.name
                    )
                    calendarIntent.putExtra("allDay", true)
                    calendarIntent.putExtra("rule", "FREQ=YEARLY")
                    startActivity(
                        Intent.createChooser(
                            calendarIntent,
                            "Choose calendar provider"
                        )
                    )
                }
            }
            binding.showTicketsButton -> {
                if (viewModel.ticket != null && viewModel.ticket?.isNotEmpty()!!) {
                    PdfPreviewDialog(viewModel.ticket?.get(0)!!)
                        .show(
                            supportFragmentManager,
                            "PdfPreviewDialog"
                        )
                } else if (viewModel.order?.ticketUrls != null && viewModel.order?.ticketUrls?.isNotEmpty()!!) {
//                    try {
//                        startActivity(
//                            Intent(
//                                Intent.ACTION_VIEW,
//                                Uri.parse(viewModel.order?.ticketUrls?.get(0))
//                            )
//                        )
//                    } catch (e: Throwable) {
//                        e.printStackTrace()
//                    }
                    OrderLinksBottomFragment(viewModel.order?.ticketUrls!!).show(
                        supportFragmentManager,
                        OrderLinksBottomFragment.TAG
                    )
                } else {
                    Toast.makeText(
                        this@DetailsOrderActivity,
                        "Your ticket currently is unavailable",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun loadImageAndSet(url: String?) {
        val request = ImageRequest.Builder(this)
            .data(url)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .placeholder(R.drawable.placeholder_event)
            .error(R.drawable.placeholder_event)
            .target(
                onSuccess = { result ->
                    viewModel.isLoading.postValue(false)
                    binding.collapsingImageView.setImageDrawable(result)
                    binding.orderDetailsMain.visibility = View.VISIBLE
                },
                onError = {
                    binding.orderDetailsMain.visibility = View.VISIBLE
                }
            )
            .build()
        imageLoader.enqueue(request)
    }

    private fun setupViews(order: OrderDetails?, userLocation: LatLng?, eventLocation: LatLng?) {
        binding.notesText.text =
            Enums.TicketDescType.getDescByType(Enums.TicketDescType.getType(viewModel.order?.ticketFormat!!))

        binding.ticketPerformerName.text = order?.name
        binding.performerName.text = order?.name
        binding.ticketPlace.text = order?.venue?.getFullPlace()

        val date = if (order?.time == null) {
            "2020-01-22T19:00:00Z"
        } else {
            order.time
        }

        binding.performerDate.text = DateHelper.dateForHomeEvent(date)
        binding.placeName.text = order?.venue?.name

        val style = when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                Style.DARK
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                Style.MAPBOX_STREETS
            }
            else -> {
                Style.MAPBOX_STREETS
            }
        }

        val eventPosition =
            if (eventLocation?.longitude != null) {
                com.mapbox.geojson.Point.fromLngLat(eventLocation.longitude, eventLocation.latitude)
            } else {
                com.mapbox.geojson.Point.fromLngLat(-118.253094, 34.025207)
            }
        val userPoint =
            if (userLocation?.longitude == null) {
                com.mapbox.geojson.Point.fromLngLat(-118.253094, 34.025207)
            } else {
                com.mapbox.geojson.Point.fromLngLat(userLocation.longitude, userLocation.latitude)

            }

        val polygon = Polygon.fromLngLats(arrayListOf(listOf(eventPosition, userPoint)))
        val cameraPosition =
            mapboxMap?.cameraForGeometry(polygon, EdgeInsets(20.0, 40.0, 20.0, 40.0))

        mapboxMap?.also {
            it.setCamera(
                cameraPosition!!
            )
        }?.loadStyle(
            styleExtension = style(style) {
                +image(BLUE_ICON_ID) {
                    bitmap(ResourcesCompat.getDrawable(resources, R.drawable.pin_other, null)?.toBitmap()!!)
                }
                +geoJsonSource(SOURCE_ID) {
                    geometry(eventPosition)
                }
                +symbolLayer(LAYER_ID, SOURCE_ID) {
                    iconImage(BLUE_ICON_ID)
                    iconAnchor(IconAnchor.BOTTOM)
                }
                +geoJsonSource(SOURCE_ID +1) {
                    geometry(userPoint)
                }
                +image(BLUE_ICON_ID +1) {
                    bitmap(ResourcesCompat.getDrawable(resources, R.drawable.black_pin_bg, null)!!.toBitmap())
                }
                +symbolLayer(LAYER_ID +1, SOURCE_ID +1) {
                    iconImage(BLUE_ICON_ID +1)
                    iconAnchor(IconAnchor.CENTER)
                }
            }
        )
    }

    override fun invoke(p1: View) {
        when (p1) {
            binding.supportButton -> {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "message/rfc822"
                i.putExtra(Intent.EXTRA_EMAIL, arrayOf("tonydarkowork@evenz.co"))
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email")
                i.putExtra(Intent.EXTRA_TEXT, "body of email")
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."))
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        this, "There are no email clients installed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            binding.navigateButton -> {
                if (isMapsAvailable()) {
                    if (viewModel.eventLocation != null && viewModel.userLocation != null) {
                        val uri =
                            "http://maps.google.com/maps?f=d&hl=en&saddr=" + viewModel.userLocation?.latitude.toString() + "," + viewModel.userLocation?.longitude.toString() + "&daddr=" + viewModel.eventLocation?.latitude.toString() + "," + viewModel.eventLocation?.longitude.toString()
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(Intent.createChooser(intent, "Select an application"))
                    } else {
                        Toast.makeText(
                            this,
                            "Location is null", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    launchGoogleUri(Uri.parse("market://details?id=com.google.android.apps.maps"))
                }
            }
        }
    }

    private fun writeFileOnInternalStorage(sFileName: String, sBody: String?) {
        val dir: File = cacheDir
        val file = File(dir.absolutePath)
        if (!file.exists()) {
            file.mkdir()
        }
        try {
            val gpxfile = File(file, sFileName)
            val writer = FileWriter(gpxfile)
            writer.append(sBody)
            writer.flush()
            writer.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun doPhotoPrint(ticket: String, order: OrderDetails?) {
        val base64Cleared = ticket.replace("\\n", "")
        val fileName = "${order?.name}_${order?.time}.pdf"

        writeFileOnInternalStorage(fileName, base64Cleared)

        val dir: File = cacheDir

        val file = File(dir.absolutePath, fileName)
        val fos = FileOutputStream(file)

        val bytes = base64Cleared.toByteArray(charset("UTF-8"))
        val decoded = Base64.decode(bytes, Base64.DEFAULT)
        fos.write(decoded)
        fos.close()

        val printManager =
            getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = getString(R.string.app_name) + " Document"
        printManager.print(jobName, CloudPrintDocumentAdapter(file), null)
    }

    //    private fun launchUberUri(uri: Uri) {
//        val showUri = Intent(Intent.ACTION_VIEW, uri)
//        showUri.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
//        try {
//            startActivity(showUri)
//        } catch (e: ActivityNotFoundException) {
//            e.printStackTrace()
//            try {
//                startActivity(Intent(Intent.ACTION_VIEW, uri))
//            } catch (e1: ActivityNotFoundException) {
//                e1.printStackTrace()
//                startActivity(
//                    Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("https://play.google.com/store/apps/details?id=com.ubercab")
//                    )
//                )
//            }
//        }
//    }

    private fun launchGoogleUri(uri: Uri) {
        val showUri = Intent(Intent.ACTION_VIEW, uri)
        showUri.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(showUri)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun isMapsAvailable(): Boolean {
        val packageName = "com.google.android.apps.maps"
        val flag = 0
        return try {
            val appInfo = packageManager?.getApplicationInfo(packageName, flag)
            appInfo?.enabled == true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    //    private fun isUberAvailable(): Boolean {
//        val packageName = "com.ubercab"
//        val flag = 0
//        return try {
//            val appInfo = packageManager?.getApplicationInfo(packageName, flag)
//            appInfo?.enabled == true
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//            false
//        }
//    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        private const val BLUE_ICON_ID = "blue"
        private const val SOURCE_ID = "source_id"
        private const val LAYER_ID = "layer_id"
    }
}