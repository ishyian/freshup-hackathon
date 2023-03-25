package com.melitopolcherry.hackathon.features.bottom

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.BottomSheetDetailsMapBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.geojson.Point
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

@AndroidEntryPoint
class DetailsMapBottomFragment(private val event: EventFull?, private var userLocation: LatLng?) :
    BaseBottomFragmentDialog(), View.OnClickListener {

    private var mapboxMap: MapboxMap? = null
    lateinit var binding: BottomSheetDetailsMapBinding

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDetailsMapBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigateButton.setOnClickListener(this)
        binding.requestButton.setOnClickListener(this)

        binding.placeName.text = event?.venue?.getFullPlace()

        try {
            val loc1 = Location("")
            loc1.latitude = userLocation?.latitude!!
            loc1.longitude = userLocation?.longitude!!
            val loc2 = Location("")
            loc2.latitude = event?.venue?.latitude ?: 34.025207
            loc2.longitude = event?.venue?.longitude ?: -118.253094

            val distance = loc1.distanceTo(loc2)
            binding.distanceTv.text =
                getString(R.string.distance_template, kotlin.math.ceil(distance * MILES_COOF))
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        mapboxMap = binding.mapView.getMapboxMap()
        binding.mapView.gestures.pitchEnabled = false
        binding.mapView.gestures.scrollEnabled = false
        binding.mapView.gestures.rotateEnabled = false
        binding.mapView.gestures.doubleTapToZoomInEnabled = false
        binding.mapView.scalebar.enabled = false

        val style =
            when (requireContext().resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
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
            if (event?.venue?.longitude != null) {
                Point.fromLngLat(event.venue?.longitude!!, event.venue?.latitude!!)
            } else {
                Point.fromLngLat(-118.253094, 34.025207)
            }
        val userPoint =
            if (userLocation == null || userLocation?.longitude == null) {
                userLocation = LatLng(34.025207, -118.253094)
                Point.fromLngLat(-118.253094, 34.025207)
            } else {
                Point.fromLngLat(userLocation?.longitude!!, userLocation?.latitude!!)
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
                    bitmap(
                        BitmapFactory.decodeResource(
                            resources,
                            resources.getIdentifier(
                                "pin_${Enums.EventCategories.categoryOf(event?.eventCategory!!)}",
                                "drawable",
                                requireContext().packageName
                            )
                        )
                    )
                }
                +geoJsonSource(SOURCE_ID) {
                    geometry(eventPosition)
                }
                +symbolLayer(LAYER_ID, SOURCE_ID) {
                    iconImage(BLUE_ICON_ID)
                    iconAnchor(IconAnchor.BOTTOM)
                }
                +geoJsonSource(SOURCE_ID + 1) {
                    geometry(userPoint)
                }
                +image(BLUE_ICON_ID + 1) {
                    bitmap(
                        ResourcesCompat.getDrawable(resources, R.drawable.black_pin_bg, null)!!
                            .toBitmap()
                    )
                }
                +symbolLayer(LAYER_ID + 1, SOURCE_ID + 1) {
                    iconImage(BLUE_ICON_ID + 1)
                    iconAnchor(IconAnchor.CENTER)
                }
            }
        )
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_details_map, null)
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

    override fun onClick(p0: View?) {
        when (p0) {
            binding.requestButton -> {
                if (isUberAvailable()) {
                    launchUberUri(
                        Uri.parse(
                            "uber://?client_id=${resources.getString(R.string.uber_app_id)}>&action=setPickup" +
                                "&pickup[latitude]=${0.0}&pickup[longitude]=${0.0}&pickup[nickname]=UberHQ" +
                                "&pickup[formatted_address]=1455%20Market%20St%2C%20San%20Francisco%2C%20CA%2094103&" +
                                "dropoff[latitude]=${0.0}&dropoff[longitude]=${0.0}&dropoff[nickname]=Coit%20Tower&" +
                                "dropoff[formatted_address]=1%20Telegraph%20Hill%20Blvd%2C%20San%20Francisco%2C%20CA%2094133&" +
                                "product_id=a1111c8c-c720-46c3-8534-2fcdd730040d&link_text=View%20team%20roster&partner_deeplink=partner%3A%2F%2Fteam%2F9383\n"
                        )
                    )
                } else {
                    launchUberUri(Uri.parse("market://details?id=com.ubercab"))
                }
            }
            binding.navigateButton -> {
                if (isMapsAvailable()) {
                    if (event?.venue != null && userLocation != null) {
                        val uri =
                            "http://maps.google.com/maps?f=d&hl=en&saddr=" + userLocation?.latitude.toString() + "," + userLocation?.longitude.toString() + "&daddr=" + event.venue?.latitude.toString() + "," + event.venue?.longitude.toString()
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(Intent.createChooser(intent, "Select an application"))
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Location is null", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    launchGoogleUri(Uri.parse("market://details?id=com.google.android.apps.maps"))
                }
            }
        }
    }

    private fun launchUberUri(uri: Uri) {
        val showUri = Intent(Intent.ACTION_VIEW, uri)
        showUri.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(showUri)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            try {
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            } catch (e1: ActivityNotFoundException) {
                e1.printStackTrace()
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.ubercab")
                    )
                )
            }
        }
    }

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
            val appInfo = activity?.packageManager?.getApplicationInfo(packageName, flag)
            appInfo?.enabled == true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun isUberAvailable(): Boolean {
        val packageName = "com.ubercab"
        val flag = 0
        return try {
            val appInfo = activity?.packageManager?.getApplicationInfo(packageName, flag)
            appInfo?.enabled == true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(event: EventFull?, userLocation: LatLng?) =
            DetailsMapBottomFragment(event, userLocation)

        private const val BLUE_ICON_ID = "blue"
        private const val SOURCE_ID = "source_id"
        private const val LAYER_ID = "layer_id"
        private const val MILES_COOF = 0.000621371
        const val TAG = "DetailsMapBottomFragment"
    }
}