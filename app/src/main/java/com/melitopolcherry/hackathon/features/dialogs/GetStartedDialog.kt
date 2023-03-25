package com.melitopolcherry.hackathon.features.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.databinding.DialogUnregistredBinding

class GetStartedDialog(private val onDialogResult: (Boolean) -> Unit) : DialogFragment() {

    lateinit var binding: DialogUnregistredBinding

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
        binding = DialogUnregistredBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allowDialogButton.text = getString(R.string.get_started_button_text)
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
