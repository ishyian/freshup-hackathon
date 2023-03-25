package com.melitopolcherry.hackathon.core.extensions

import android.content.Context
import android.content.res.Configuration
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.style

fun MapboxMap.setupDarkTheme(context: Context) {
    when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> {
            loadStyle(style(Style.DARK) {})
        }
        Configuration.UI_MODE_NIGHT_NO -> {
            loadStyle(style(Style.MAPBOX_STREETS) {})
        }
        else -> {
            loadStyle(style(Style.MAPBOX_STREETS) {})
        }
    }
}

fun MapboxMap.getRenderedQueryGeometry(point: Point): RenderedQueryGeometry {
    return RenderedQueryGeometry(pixelForCoordinate(point))
}