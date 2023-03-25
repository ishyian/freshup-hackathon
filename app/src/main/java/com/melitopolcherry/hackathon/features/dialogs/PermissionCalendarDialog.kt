package com.melitopolcherry.hackathon.features.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.databinding.DialogLocationAccessBinding

class PermissionCalendarDialog(private val onDialogResult: (Boolean) -> Unit) : DialogFragment() {

    lateinit var binding: DialogLocationAccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLocationAccessBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogImage.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.permission_calendar
            )
        )
        binding.dialogTitle.text = getString(R.string.permission_dialog_calendar_title)
        binding.dialogText.text = getString(R.string.permission_dialog_calendar_text)
        binding.allowDialogButton.text = getString(R.string.permission_dialog_calendar_button_text)
        binding.allowDialogButton.setOnClickListener {
            onDialogResult.invoke(true)
            dismiss()
        }
        binding.closeDialogButton.setOnClickListener {
            onDialogResult.invoke(false)
            dismiss()
        }
        dialog?.show()
    }
}