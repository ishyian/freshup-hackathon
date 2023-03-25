package com.melitopolcherry.hackathon.features.bottom

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.melitopolcherry.hackathon.adapters.callbacks.AddPromocodeCallback
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.databinding.BottomSheetAddPromocodeBinding
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPromocodeBottomFragment(private val addPromocodeCallback: AddPromocodeCallback.Callback) :
    BaseBottomFragmentDialog(), View.OnClickListener, TextView.OnEditorActionListener {

    lateinit var binding: BottomSheetAddPromocodeBinding
    private val viewModel: AddPromocodeViewModel by viewModels()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.addPromocodeCallback = addPromocodeCallback
        viewModel.showMessage.observe(this) {
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_LONG
            ).show()
        }
        viewModel.processError.observe(this) {
        }
        viewModel.dismissView.observe(this) {
            if (it) {
                dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddPromocodeBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val displayMetrics: DisplayMetrics = activity?.resources?.displayMetrics!!
        val height: Int = displayMetrics.heightPixels
        val view =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_add_promocode, null)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        if (height != 0) {
            params.height = height / 2
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
        binding.promocodeEditText.setOnEditorActionListener(this)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            validateDataAndUpdate()
            return true
        }
        return false
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.saveButton -> {
                validateDataAndUpdate()
            }
        }
    }

    private fun validateDataAndUpdate() {
        val promocode = binding.promocodeEditText.text?.toString()?.trim()
        if (promocode?.length!! >= 4) {
            viewModel.checkPromoCode(promocode)
        }
    }

    companion object {
        const val TAG = "AddPromocodeBottomFragment"
    }
}