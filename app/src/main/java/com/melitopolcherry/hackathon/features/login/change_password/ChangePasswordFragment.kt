package com.melitopolcherry.hackathon.features.login.change_password

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.base.AuthFragment
import com.melitopolcherry.hackathon.custom_view.showInfoDialog
import com.melitopolcherry.hackathon.databinding.FragmentChangePasswordBinding
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : AuthFragment<FragmentChangePasswordBinding>(),
    View.OnClickListener, TextView.OnEditorActionListener {

    private val viewModel: ChangePasswordViewModel by viewModels()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentChangePasswordBinding {
        return FragmentChangePasswordBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.goToMainScreen.observe(this) {
            if (it) {
                if ((activity as LoginActivity).loginType != 111) {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
                activity?.finish()
            }
        }
        viewModel.showErrorMessage.observe(this) {
            showInfoDialog(it)
        }
        viewModel.resetTimerText.observe(this) {
            if (it < 60) {
                binding.resendTimerButton.text = String.format("%02d:%02d", 0, it)
            } else {
                binding.resendTimerButton.text = getString(R.string.resend_button_text)
            }
        }
        viewModel.resetTimerBlock.observe(this) {
            binding.resendTimerButton.isClickable = !it
        }
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener(this)
        binding.backButton.root.setOnClickListener(this)
        binding.resendTimerButton.setOnClickListener(this)
        binding.codeEditText.setOnEditorActionListener(this)
        binding.emailTextView.text = arguments?.getString("email")

        binding.resendTimerButton.isClickable = false
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            val email = binding.emailTextView.text.toString().trim()
            val newPassword = binding.passwordEditText.text.toString().trim()
            val repeatPassword = binding.repeatPasswordEditText.text.toString().trim()
            val code = binding.codeEditText.text.toString().trim()

            viewModel.changePassword(email, newPassword, repeatPassword, code)
            return true
        }
        return false
    }

    override fun onClick(v: View?) {
        if (!blockButtons) {
            blockButtons = true
            when (v) {
                binding.backButton.root -> {
                    activity?.onBackPressed()
                }
                binding.nextButton -> {
                    val email = binding.emailTextView.text.toString().trim()
                    val newPassword = binding.passwordEditText.text.toString().trim()
                    val repeatPassword = binding.repeatPasswordEditText.text.toString().trim()
                    val code = binding.codeEditText.text.toString().trim()
                    viewModel.changePassword(email, newPassword, repeatPassword, code)
                }
                binding.resendTimerButton -> {
                    viewModel.resetTimer(binding.emailTextView.text.toString().trim())
                }
            }
            runDelayedUnblock()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(email: String) = ChangePasswordFragment().apply {
            arguments = Bundle().apply {
                putString("email", email)
            }
        }

        const val TAG = "ChangePasswordFragment"
    }
}