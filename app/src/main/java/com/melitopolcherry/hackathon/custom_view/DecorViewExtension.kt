package com.melitopolcherry.hackathon.custom_view

import android.view.View
import android.view.Window

fun Window.setLightSystemsBars(background: Int) {
    clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    clearFlags(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    statusBarColor = background
    navigationBarColor = background

    val flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
}

fun Window.setDarkSystemsBars(background: Int) {
    clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    clearFlags(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    statusBarColor = background
    navigationBarColor = background

    decorView.systemUiVisibility = 0
}