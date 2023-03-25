package com.melitopolcherry.hackathon.custom_view

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog

fun AppCompatActivity.showInfoDialog(message: String): MaterialDialog? {
    var dialog: MaterialDialog? = null
    try {
        dialog = MaterialDialog(this).show {
            message(text = message)
            positiveButton(text = "Ok") {
                dismiss()
            }
            cancelable(true)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return dialog
}

fun Fragment.showInfoDialog(message: String): MaterialDialog? {
    var dialog: MaterialDialog? = null
    try {
        dialog = MaterialDialog(requireContext()).show {
            message(text = message)
            positiveButton(text = "Ok") {
                dismiss()
            }
            cancelable(true)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return dialog
}