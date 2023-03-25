package com.melitopolcherry.hackathon.features.bottom

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.databinding.BottomSheetProtectBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProtectBottomFragment : BaseBottomFragmentDialog(),
    View.OnClickListener {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    lateinit var binding: BottomSheetProtectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetProtectBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_add_promocode, null)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.skipCollapsed = true
            dialog.setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.moreButton -> {

            }
            binding.closeButton ->{
                dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.moreButton.setOnClickListener(this)
        binding.closeButton.setOnClickListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProtectBottomFragment()

        const val TAG = "ProtectBottomFragment"
    }
}