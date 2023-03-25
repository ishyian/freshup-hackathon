package com.melitopolcherry.hackathon.custom_view

import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.widget.EditText

class PhoneTextFormatter(private val editText: EditText, private val pattern: String) :
    TextWatcher {
    init {
        val maxLength = pattern.length
        editText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        val phone = StringBuilder(s)
        if (count > 0 && !isValid(phone.toString())) {
            for (i in phone.indices) {
                val c = pattern[i]
                if (c != '#' && c != phone[i]) {
                    phone.insert(i, c)
                }
            }
            editText.setText(phone)
            editText.setSelection(editText.text.length)
        }
    }

    override fun afterTextChanged(s: Editable) {}
    private fun isValid(phone: String): Boolean {
        for (i in phone.indices) {
            val c = pattern[i]
            if (c == '#') continue
            if (c != phone[i]) {
                return false
            }
        }
        return true
    }
}