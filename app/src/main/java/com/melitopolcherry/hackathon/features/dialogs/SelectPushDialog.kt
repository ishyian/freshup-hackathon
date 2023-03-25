package com.melitopolcherry.hackathon.features.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.databinding.DialogSelectPushBinding

class SelectPushDialog(val myCallback: (result: Int?) -> Unit
) : DialogFragment() {

    lateinit var binding: DialogSelectPushBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSelectPushBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.push.setOnClickListener {
            myCallback.invoke(0)
            dismiss()
        }
        binding.pushWithTitle.setOnClickListener {
            myCallback.invoke(1)
            dismiss()
        }
        binding.pushWithImg.setOnClickListener {
            myCallback.invoke(2)
            dismiss()
        }
    }

    companion object {
        var TAG = "SelectPushDialog"
    }
}