package com.melitopolcherry.hackathon.core.extensions

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources.getSystem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.melitopolcherry.hackathon.R

val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()

val Int.dp: Int get() = (this / getSystem().displayMetrics.density).toInt()

fun Fragment.showSelectorDialog(
    title: String,
    items: List<String>,
    listener: (DialogInterface, Int) -> Unit
) {
    requireContext().showSelectorDialog(title, items, listener)
}

fun Context.showSelectorDialog(
    title: String,
    items: List<String>,
    listener: (DialogInterface, Int) -> Unit
) {
    val dialog = AlertDialog.Builder(this)
    dialog.setTitle(title)
    dialog.setItems(items.toTypedArray(), listener)
    dialog.setNegativeButton(getString(R.string.cancel)) { dialogInterface: DialogInterface, _: Int ->
        dialogInterface.dismiss()
    }
    dialog.show()
}

