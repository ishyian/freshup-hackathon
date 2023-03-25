package com.melitopolcherry.hackathon.features.maps.core.presentation

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.QueryFeaturesCallback
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.observable.eventdata.CameraChangedEventData
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.addLayerBelow
import com.mapbox.maps.extension.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.delegates.listeners.OnCameraChangeListener
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.core.extensions.getRenderedQueryGeometry
import com.melitopolcherry.hackathon.core.extensions.setupDarkTheme
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.model.EventItem
import com.melitopolcherry.hackathon.data.model.FACULTIES
import com.melitopolcherry.hackathon.data.model.Faculty
import com.melitopolcherry.hackathon.data.model.Marker
import com.melitopolcherry.hackathon.data.model.Place
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.DialogEventBinding
import com.melitopolcherry.hackathon.databinding.DialogPlaceBinding
import com.melitopolcherry.hackathon.databinding.FragmentMapBinding
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.features.dialogs.LocationDisableDialog
import com.melitopolcherry.hackathon.features.dialogs.PermissionBlockedLocationDialog
import com.melitopolcherry.hackathon.features.dialogs.PermissionLocationDialog
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.features.services.LocationUtils
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(), Observer<List<Marker>>,
    LocationUtils.LocationCallback, OnMapClickListener, OnCameraChangeListener {
    private val viewModel: MapViewModel by viewModels()

    private val mapboxMap: MapboxMap by lazy {
        binding.mapView.getMapboxMap()
    }

    private val locationUtils: LocationUtils by lazy {
        LocationUtils(requireContext(), this)
    }

    private val rxPermissions by lazy {
        RxPermissions(this)
    }

    @Inject
    lateinit var accountManager: IAccountManager

    private val faculty: Faculty by lazy {
        FACULTIES.first { it.id == accountManager.fetchFaculty()?.facultyId }
    }

    private val queryOptions = RenderedQueryOptions(listOf(ICON_LAYER_ID), null)

    private var source: GeoJsonSource? = null
    private var animationSource: GeoJsonSource? = null
    private var featureCollection: FeatureCollection? = null
    private var animatedFeatureCollection: FeatureCollection? = null

    private var selectedFeature: Feature? = null
    private var animationLayer: CircleLayer? = null
    private var circleAnimator: ValueAnimator? = null

    private var pinLayer: SymbolLayer? = null
    private var iconLayer: SymbolLayer? = null
    private var isIconsAdded = false
    private var isNoGPSShowed = false

    private var cameraPositionCallback: ICameraPositionCallback.Callback? = null

    private val onRenderedFeaturesReceived = QueryFeaturesCallback { featuresResult ->
        if (!featuresResult.value.isNullOrEmpty()) {
            val title = featuresResult.value?.first()?.feature?.getStringProperty(PROPERTY_TITLE)
            val type = featuresResult.value?.first()?.feature?.getStringProperty(PROPERTY_TYPE)
            val featureList = featureCollection?.features()
            featureList?.forEachIndexed { _, feature ->
                when (type) {
                    "event" -> {
                        if (feature.getStringProperty(PROPERTY_TITLE) == title) {
                            val builder = AlertDialog.Builder(requireContext())
                            val binding = DialogEventBinding.inflate(LayoutInflater.from(context))
                            binding.textAddress.text = feature.getStringProperty(PROPERTY_ADDRESS)
                            binding.textName.text = feature.getStringProperty(PROPERTY_TITLE)
                            binding.textType.text = feature.getStringProperty(PROPERTY_SUBTYPE)
                            binding.textTeacherName.text = feature.getStringProperty(PROPERTY_TEACHER_NAME)
                            builder.setView(binding.root)
                            builder.show()
                        }
                    }
                    "place" -> {
                        if (feature.getStringProperty(PROPERTY_TITLE) == title) {
                            val builder = AlertDialog.Builder(requireContext())
                            val binding = DialogPlaceBinding.inflate(LayoutInflater.from(context))
                            Glide.with(requireContext()).load(feature.getStringProperty(PROPERTY_IMAGE))
                                .into(binding.imagePlace)
                            binding.textAddress.text = feature.getStringProperty(PROPERTY_ADDRESS)
                            binding.textName.text = feature.getStringProperty(PROPERTY_TITLE)
                            binding.textType.text = feature.getStringProperty(PROPERTY_SUBTYPE)
                            binding.btnRoute.setOnClickListener {
                                if (isMapsAvailable()) {
                                    val uri =
                                        "http://maps.google.com/maps?f=d&hl=en" + "&daddr=" + feature.getStringProperty(
                                            PROPERTY_LATITUDE
                                        ) + "," + feature.getStringProperty(
                                            PROPERTY_LONGITUDE
                                        )
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                    startActivity(Intent.createChooser(intent, "Select an application"))

                                } else {
                                    launchGoogleUri(Uri.parse("market://details?id=com.google.android.apps.maps"))
                                }
                            }
                            builder.setView(binding.root)
                            builder.show()
                        }
                    }
                }
            }
        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMapBinding.inflate(inflater, container, false)

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapboxMap.setupDarkTheme(requireContext())
        mapboxMap.addOnMapClickListener(this)
        mapboxMap.addOnCameraChangeListener(this)
        mapboxMap.setCamera(moveCameraTo(null))
        val act = requireActivity() as MainActivity
        observe(viewModel.markers) {
            addMarker(it)
        }

        observe(viewModel.markersLiveData.getEvents()) {
            if (it.isNotEmpty()) {
                viewModel.showMarkers(act.events, act.places, it)
            } else {
                viewModel.showMarkers(act.events, act.places)
            }
        }

        binding.mapView.scalebar.enabled = false
        checkLocationPermissions()
        updateLocation(LatLng(faculty.latitude, faculty.longitude))
        addVenueMarker(faculty)

        viewModel.showMarkers(act.events, act.places)
    }

    private fun checkLocationPermissions() {
        val isPermissionsGranted = rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
            rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (isPermissionsGranted) {
            initLocationComponent()
        }
    }

    override fun providerDisabled(boolean: Boolean) {
        showLocationDisabledDialogIfNeed()
        val eventsFiltersConfig = viewModel.filtersConfigLiveData.getFilters().value ?: EventsFilters()
        viewModel.userLocation = viewModel.accountManager.getLocation()
        if (viewModel.userLocation != null && viewModel.userLocation?.latitude != null) {
            if (eventsFiltersConfig.latitude == viewModel.userLocation?.latitude.toString() && eventsFiltersConfig.longitude == viewModel.userLocation?.longitude.toString()) {
                return
            }
            eventsFiltersConfig.latitude = viewModel.userLocation?.latitude.toString()
            eventsFiltersConfig.longitude = viewModel.userLocation?.longitude.toString()
            viewModel.filtersConfigLiveData.getFilters().value = eventsFiltersConfig

            updateLocation(viewModel.userLocation!!)
        } else {
            setDefaultLocation()
        }
    }

    private fun showLocationDisabledDialogIfNeed() {
        if (!isNoGPSShowed) {
            isNoGPSShowed = true
            LocationDisableDialog { isAllowed ->
                if (isAllowed) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }.show(childFragmentManager, "LocationDisableDialog")
        }
    }

    private fun setDefaultLocation() {
        val eventsFiltersConfig = viewModel.filtersConfigLiveData.getFilters().value ?: EventsFilters()
        eventsFiltersConfig.latitude = DEFAULT_LATITUDE.toString()
        eventsFiltersConfig.longitude = DEFAULT_LONGITUDE.toString()
        eventsFiltersConfig.selectedCity = null
        viewModel.filtersConfigLiveData.getFilters().value = eventsFiltersConfig
        updateLocation(LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE))
    }

    override fun onLocationChanged(location: LatLng) {
        viewModel.userLocation = location
        val eventsFiltersConfig = viewModel.filtersConfigLiveData.getFilters().value ?: EventsFilters()

        eventsFiltersConfig.latitude = location.latitude.toString()
        eventsFiltersConfig.longitude = location.longitude.toString()
        eventsFiltersConfig.selectedCity = null

        viewModel.filtersConfigLiveData.getFilters().value = eventsFiltersConfig
        updateLocation(LatLng(location.latitude, location.longitude))

        locationUtils.removeLocationListener()
        viewModel.accountManager.saveLocation(location)
    }

    override fun onChanged(events: List<Marker>?) {
        Timber.d("events ${events?.size}")
        if (!events.isNullOrEmpty()) {
            addMarker(events)
        } else {
            source?.featureCollection(
                FeatureCollection.fromFeatures(listOf())
            )
            animationSource?.featureCollection(
                FeatureCollection.fromFeatures(listOf())
            )
        }
    }

    fun searchInThisPlace() {
        mapboxMap.cameraState.center.let {
            cameraPositionCallback?.onChange(LatLng(it.latitude(), it.longitude()))
        }
    }

    override fun onMapClick(point: Point): Boolean {
        mapboxMap.queryRenderedFeatures(
            mapboxMap.getRenderedQueryGeometry(point),
            queryOptions,
            onRenderedFeaturesReceived
        )
        return false
    }

    private fun unselectFeature(feature: Feature) {
        feature.properties()?.addProperty(PROPERTY_SELECTED, 0)
        selectedFeature = null
        refreshSource()
    }

    fun unselectLast() {
        selectedFeature?.properties()?.addProperty(PROPERTY_SELECTED, 0)
        refreshSource()
        selectedFeature = null
    }

    private fun refreshSource() {
        if (source != null && featureCollection != null) {
            source?.featureCollection(featureCollection!!)
        }
    }

    private fun selectFeature(feature: Feature) {
        when {
            selectedFeature == null -> {
                feature.properties()?.addProperty(PROPERTY_SELECTED, 1)
                selectedFeature = feature
            }
            feature == selectedFeature -> {
                unselectFeature(feature)
            }
            feature != selectedFeature -> {
                unselectFeature(selectedFeature!!)
                selectedFeature = feature
                feature.properties()?.addProperty(PROPERTY_SELECTED, 1)
            }
        }
    }

    override fun onCameraChanged(eventData: CameraChangedEventData) {
        val position = mapboxMap.cameraState.center
        if (viewModel.userLocation?.latitude != null
            && viewModel.filtersConfigLiveData.getFilters().value?.radius != null
        ) {
            val loc1 = Location("")
            loc1.latitude = position.latitude()
            loc1.longitude = position.longitude()
            val loc2 = Location("")
            loc2.latitude =
                viewModel.filtersConfigLiveData.getFilters().value?.latitude?.toDouble()!!
            loc2.longitude =
                viewModel.filtersConfigLiveData.getFilters().value?.longitude?.toDouble()!!

            val distance = loc1.distanceTo(loc2)

            if (distance * MILES_COOF >= viewModel.filtersConfigLiveData.getFilters().value?.radius!!) {
                cameraPositionCallback?.showButton()
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun addMarker(events: List<Marker>) {
        Timber.d("add Marker")
        if (events.isEmpty()) {
            return
        }

        if (!isIconsAdded) {
            mapboxMap.getStyle {
                addToStyleIcons(it)
            }
        }

        val bounds = arrayListOf<Point>()
        val markerCoordinates = arrayListOf<Feature>()
        val animatedMarkerCoordinates = arrayListOf<Feature>()

        for (event in events) {
            when (event) {
                is EventItem -> {
                    val point = Point.fromLngLat(
                        event.longitude,
                        event.latitude
                    )
                    bounds.add(point)
                    val feature =
                        Feature.fromGeometry(point)
                    if (!markerCoordinates.contains(feature)) {
                        val faculty = FACULTIES.first { it.id == event.facultyId }
                        val subtype = when (event.type) {
                            "lectures" -> "Лекція"
                            "practices" -> "Практика"
                            else -> "Лабораторна"
                        }
                        feature.addStringProperty(PROPERTY_TITLE, event.subjectName)
                        feature.addStringProperty(PROPERTY_ADDRESS, "${faculty.address}, аудиторія ${event.classroom}")
                        feature.addStringProperty(PROPERTY_DATE, event.startDate)
                        feature.addStringProperty(PROPERTY_END_DATE, event.endDate)
                        feature.addStringProperty(PROPERTY_TYPE, "event")
                        feature.addStringProperty(PROPERTY_SUBTYPE, subtype)
                        feature.addStringProperty(PROPERTY_TEACHER_NAME, event.teacherName)
                        feature.properties()?.addProperty(
                            PROPERTY_CATEGORY,
                            "icon_${event.type.lowercase()}"
                        )
                        feature.properties()?.addProperty(PROPERTY_PIN, "point")
                        feature.properties()?.addProperty(PROPERTY_PIN_SELECTED, "point_selected")
                        feature.properties()?.addProperty(PROPERTY_SELECTED, 0)

                        markerCoordinates.add(feature)

                    }
                }
                is Place -> {
                    val point = Point.fromLngLat(
                        event.longitude.toDouble(),
                        event.latitude.toDouble()
                    )
                    bounds.add(point)
                    val feature =
                        Feature.fromGeometry(point)
                    if (!markerCoordinates.contains(feature)) {
                        feature.addStringProperty(PROPERTY_TITLE, event.name)
                        val subtype = when (event.type) {
                            "hospital" -> "Медпункт"
                            "hostel" -> "Гуртожиток"
                            "gym" -> "Спортзал"
                            "diner" -> "Їдальня"
                            else -> "Інше"
                        }
                        feature.addStringProperty(PROPERTY_TITLE, event.name)
                        feature.addStringProperty(PROPERTY_ADDRESS, faculty.address)
                        feature.addStringProperty(PROPERTY_SUBTYPE, subtype)
                        feature.addStringProperty(PROPERTY_TYPE, "place")
                        feature.addStringProperty(PROPERTY_LATITUDE, event.latitude)
                        feature.addStringProperty(PROPERTY_IMAGE, event.image)
                        feature.addStringProperty(PROPERTY_LONGITUDE, event.longitude)
                        feature.properties()?.addProperty(
                            PROPERTY_CATEGORY,
                            "icon_${event.type.lowercase()}"
                        )
                        feature.properties()?.addProperty(PROPERTY_PIN, "point")
                        feature.properties()?.addProperty(PROPERTY_PIN_SELECTED, "point_selected")
                        feature.properties()?.addProperty(PROPERTY_SELECTED, 0)

                        markerCoordinates.add(feature)

                    }
                }
            }
        }
        Timber.d("🤟🏿${events.size} ||")
        animatedFeatureCollection = FeatureCollection.fromFeatures(animatedMarkerCoordinates)
        setupData(FeatureCollection.fromFeatures(markerCoordinates)!!)
    }

    private fun addVenueMarker(faculty: Faculty) {
        if (!isIconsAdded) {
            mapboxMap.getStyle {
                addToStyleIcons(it)
            }
        }

        val bounds = arrayListOf<Point>()
        val markerCoordinates = arrayListOf<Feature>()
        val animatedMarkerCoordinates = arrayListOf<Feature>()

        val point = Point.fromLngLat(
            faculty.longitude,
            faculty.latitude
        )
        bounds.add(point)
        val feature =
            Feature.fromGeometry(point)
        if (!markerCoordinates.contains(feature)) {
            feature.addStringProperty(PROPERTY_TITLE, faculty.id)
            feature.addStringProperty(PROPERTY_TYPE, "faculty")
            feature.properties()?.addProperty(
                PROPERTY_CATEGORY,
                "icon_venue"
            )
            feature.properties()?.addProperty(
                PROPERTY_CATEGORY_SELECTED,
                "icon_selected_venue"
            )
            feature.properties()?.addProperty(PROPERTY_PIN, "point")
            feature.properties()?.addProperty(PROPERTY_PIN_SELECTED, "point_selected")
            feature.properties()?.addProperty(PROPERTY_SELECTED, 0)
            markerCoordinates.add(feature)
        } else {

        }
        animatedFeatureCollection = FeatureCollection.fromFeatures(animatedMarkerCoordinates)
        setupData(FeatureCollection.fromFeatures(markerCoordinates)!!)
    }

    private fun setupData(collection: FeatureCollection) {
        try {
            featureCollection = collection
            mapboxMap.getStyle { style ->
                if (!style.styleSourceExists(SOURCE_ID)) {
                    setupSource(style)
                } else {
                    source?.featureCollection(collection)
                    animationSource?.featureCollection(collection)
                }
                setupIconLayer(style)
                setupPinLayer(style)
                try {
                    //setupAnimationLayer(style)
                } catch (e: Throwable) {
                    Timber.d("💎 ANIMATION")
                    e.printStackTrace()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun setupSource(loadedMapStyle: Style) {
        Timber.d(
            "🤟🏿SOURCE ADDED${featureCollection?.features()?.size}|||||" + SOURCE_ID
        )
        source =
            GeoJsonSource(GeoJsonSource.Builder(SOURCE_ID)).featureCollection(featureCollection!!)
        animationSource =
            GeoJsonSource(
                GeoJsonSource.Builder(ANIMATION_SOURCE_ID)
            ).featureCollection(animatedFeatureCollection!!)
        source?.let {
            loadedMapStyle.addSource(it)
        }
        animationSource?.let {
            loadedMapStyle.addSource(it)
        }
    }

    private fun setupPinLayer(loadedMapStyle: Style) {
        pinLayer = SymbolLayer(PIN_LAYER_ID, SOURCE_ID).iconImage(
            Expression.match(
                Expression.get(PROPERTY_SELECTED), Expression.literal(0),
                Expression.get(PROPERTY_PIN), Expression.get(PROPERTY_PIN_SELECTED)
            )
        ).iconAllowOverlap(true)
        pinLayer?.let {
            loadedMapStyle.removeStyleLayer(PIN_LAYER_ID)
            loadedMapStyle.addLayerBelow(it, ICON_LAYER_ID)
        }
    }

    private fun setupIconLayer(loadedMapStyle: Style) {
        iconLayer =
            SymbolLayer(ICON_LAYER_ID, SOURCE_ID).iconImage(
                Expression.match(
                    Expression.get(PROPERTY_SELECTED),
                    Expression.literal(0),
                    Expression.get(PROPERTY_CATEGORY),
                    Expression.get(PROPERTY_CATEGORY_SELECTED)
                )
            ).iconAnchor(IconAnchor.BOTTOM).iconAllowOverlap(true)
        iconLayer?.let {
            loadedMapStyle.removeStyleLayer(ICON_LAYER_ID)
            loadedMapStyle.addLayer(it)
        }
    }

    fun updateLocation(location: LatLng) {
        val cameraPosition = CameraOptions.Builder()
            .zoom(ZOOM_LEVEL)
            .center(Point.fromLngLat(location.longitude, location.latitude))
            .build()
        mapboxMap.setCamera(cameraPosition)
        viewModel.userLocation = location
    }

    private fun moveCameraTo(position: LatLng?): CameraOptions {
        var pos: LatLng? = position
        if (position == null) {
            pos = LatLng(34.03792450289224, -118.26101313107974)
        }
        return CameraOptions.Builder()
            .zoom(ZOOM_LEVEL)
            .center(Point.fromLngLat(pos?.longitude!!, pos.latitude))
            .build()
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.mapbox_user_puck_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
    }

    fun moveToMyLocation() {
        rxPermissions.request(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )?.subscribe({ granted ->
                         if (granted) {
                             val userLocation = viewModel.accountManager.getLocation()
                             if (userLocation?.longitude != null) {
                                 mapboxMap.setCamera(
                                     moveCameraTo(
                                         LatLng(
                                             userLocation.latitude,
                                             userLocation.longitude
                                         )
                                     )
                                 )
                             } else {
                                 mapboxMap.setCamera(
                                     moveCameraTo(
                                         null
                                     )
                                 )
                             }

                         } else {
                             rxPermissions.shouldShowRequestPermissionRationale(
                                 requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.ACCESS_COARSE_LOCATION
                             )?.subscribe({
                                              if (it) {
                                                  PermissionLocationDialog { dialogResult ->
                                                      if (dialogResult) {
                                                          requestLocation()
                                                      } else {
                                                          setDefaultLocation()
                                                      }
                                                  }.show(
                                                      childFragmentManager,
                                                      "PermissionLocationDialog"
                                                  )
                                              } else {
                                                  PermissionBlockedLocationDialog { dialogResult ->
                                                      if (dialogResult) {
                                                          openLocationSettings()
                                                      } else {
                                                          setDefaultLocation()
                                                      }
                                                  }.show(
                                                      childFragmentManager,
                                                      "PermissionBlockedLocationDialog"
                                                  )
                                              }
                                          }, {
                                              (activity as MainActivity).processError(it) {}
                                          })

                         }
                     }) { e -> (activity as MainActivity).processError(e) {} }
    }

    private fun addToStyleIcons(style: Style) {
        Timber.d("📽 ICONS ADDED")

        val iconNames = Enums.EventCategories.values().map { it.value }.plus("venue")
        requireContext().let {
            style.addImage(
                "point",
                ContextCompat.getDrawable(it, R.drawable.black_pin_bg)?.toBitmap()!!
            )
            style.addImage(
                "point_selected",
                ContextCompat.getDrawable(it, R.drawable.red_pin_bg)?.toBitmap()!!
            )
            for (i in iconNames) {
                val imageName = "pin_$i"
                ContextCompat.getDrawable(
                    requireContext(), resources.getIdentifier(
                        imageName,
                        "drawable",
                        activity?.packageName
                    )
                )?.toBitmap()?.let { bitmap ->
                    style.addImage(
                        "icon_$i", bitmap
                    )
                }
            }
        }
        isIconsAdded = true
    }

    @SuppressLint("CheckResult", "MissingPermission")
    private fun requestLocation() {
        rxPermissions.request(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )?.subscribe({ granted ->
                         if (granted) {
                             locationUtils?.initLocation()
                             //                googleMap?.isMyLocationEnabled = true
                         } else {
                             rxPermissions.shouldShowRequestPermissionRationale(
                                 requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.ACCESS_COARSE_LOCATION
                             )?.subscribe({
                                              if (it) {
                                                  PermissionLocationDialog { dialogResult ->
                                                      if (dialogResult) {
                                                          requestLocation()
                                                      } else {
                                                          setDefaultLocation()
                                                      }
                                                  }.show(
                                                      childFragmentManager,
                                                      "PermissionLocationDialog"
                                                  )
                                              } else {
                                                  PermissionBlockedLocationDialog { dialogResult ->
                                                      if (dialogResult) {
                                                          openLocationSettings()
                                                      } else {
                                                          setDefaultLocation()
                                                      }
                                                  }.show(
                                                      childFragmentManager,
                                                      "PermissionBlockedLocationDialog"
                                                  )
                                              }
                                          }, {
                                              (activity as MainActivity).processError(it) {}
                                          })
                         }
                     }) { e -> (activity as MainActivity).processError(e) {} }
    }

    private fun openLocationSettings() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    companion object {
        @JvmStatic
        fun newInstance(positionChangeCallback: ICameraPositionCallback.Callback) =
            MapFragment().apply {
                this.cameraPositionCallback = positionChangeCallback
            }

        private const val MILES_COOF = 0.000621371
        private const val SOURCE_ID = "SOURCE_ID"
        private const val ANIMATION_SOURCE_ID = "ANIMATION_SOURCE_ID"
        private const val PIN_LAYER_ID = "PIN_LAYER_ID"
        private const val ICON_LAYER_ID = "ICON_LAYER_ID"
        private const val PULSING_LAYER_ID = "PULSING_LAYER_ID"

        private const val PROPERTY_SELECTED = "selected"
        private const val PROPERTY_TYPE = "type"
        private const val PROPERTY_SUBTYPE = "subtype"
        private const val PROPERTY_TITLE = "title"
        private const val PROPERTY_ADDRESS = "address"
        private const val PROPERTY_TEACHER_NAME = "teacher_name"
        private const val PROPERTY_LATITUDE = "latitude"
        private const val PROPERTY_LONGITUDE = "longitude"
        private const val PROPERTY_DATE = "date"
        private const val PROPERTY_IMAGE = "image"

        private const val PROPERTY_END_DATE = "end_date"

        private const val PROPERTY_CATEGORY = "category"
        private const val PROPERTY_PIN = "point"
        private const val PROPERTY_CATEGORY_SELECTED = "category_selected"
        private const val PROPERTY_PIN_SELECTED = "point_selected"
        private const val ZOOM_LEVEL = 17.0

        private const val DEFAULT_LATITUDE = 37.398160
        private const val DEFAULT_LONGITUDE = -122.180831

        const val TAG = "MapFragment"
    }
}