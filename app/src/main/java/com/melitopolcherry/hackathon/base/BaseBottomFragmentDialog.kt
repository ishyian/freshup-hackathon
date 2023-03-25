package com.melitopolcherry.hackathon.base

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseBottomFragmentDialog : BottomSheetDialogFragment(), View.OnClickListener {

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onClick(v: View?) = Unit

    protected fun setupClickListener(vararg clickIds: View) {
        clickIds.forEach { view ->
            view.setOnClickListener(this)
        }
    }

    protected inline fun <T> LifecycleOwner.observe(
        liveData: LiveData<T>,
        crossinline action: (t: T) -> Unit
    ) {
        liveData.observe(this) {
            it?.let { t ->
                action(t)
            }
        }
    }
}