package com.melitopolcherry.hackathon.features.bottom

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.databinding.BottomSheetUpdatePasswordBinding
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePasswordBottomFragment(private val deviceHeight: Int) :
    BaseBottomFragmentDialog(), View.OnClickListener, TextView.OnEditorActionListener {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    lateinit var binding: BottomSheetUpdatePasswordBinding
    private val viewModel: UpdatePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isLoading.observe(this) {
            binding.loadingView.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        viewModel.showMessage.observe(this) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
                .show()
        }
        viewModel.dismissView.observe(this) {
            if (it) {
                dismiss()
            }
        }
        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetUpdatePasswordBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_update_password, null)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        if (deviceHeight != 0) {
            params.height = deviceHeight / 2
        }
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.skipCollapsed = true
            dialog.setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveButton.setOnClickListener(this)
        binding.repeatPassEditText.setOnEditorActionListener(this)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            viewModel.validateUpdatePassword(
                binding.oldPassEditText.text?.trim()?.toString(),
                binding.newPassEditText.text?.trim()?.toString(),
                binding.repeatPassEditText.text?.trim()?.toString()
            )
            return true
        }
        return false
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.saveButton -> {
                viewModel.validateUpdatePassword(
                    binding.oldPassEditText.text?.trim()?.toString(),
                    binding.newPassEditText.text?.trim()?.toString(),
                    binding.repeatPassEditText.text?.trim()?.toString()
                )
            }
        }
    }

    companion object {
        const val TAG = "UpdatePasswordBottomFragment"
    }
}