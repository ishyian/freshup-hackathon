package com.melitopolcherry.hackathon.features.login.forgot_password

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.melitopolcherry.hackathon.base.AuthFragment
import com.melitopolcherry.hackathon.custom_view.showInfoDialog
import com.melitopolcherry.hackathon.databinding.FragmentForgotPasswordBinding
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.login.change_password.ChangePasswordFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : AuthFragment<FragmentForgotPasswordBinding>(),
    View.OnClickListener, TextView.OnEditorActionListener {

    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotPasswordBinding {
        return FragmentForgotPasswordBinding.inflate(inflater, container, false)
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener(loginEmailNextButton, emailEditText, backButton.root)
        initObservers()
    }

    override fun onClick(v: View?) {
        if (!blockButtons) {
            blockButtons = true
            when (v) {
                binding.backButton.root -> {
                    (activity as LoginActivity).onBackPressed()
                }
                binding.loginEmailNextButton -> {
                    val email = binding.emailEditText.text.toString().trim()
                    viewModel.sendEmail(email)
                }
            }
            runDelayedUnblock()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            val email = binding.emailEditText.text.toString().trim()
            viewModel.sendEmail(email)
            return true
        }
        return false
    }

    private fun initObservers() = with(viewModel) {
        observe(showErrorMessage, ::showInfoDialog)
        observe(processError, ::processAuthError)
        observe(showNextScreen, ::onShowNextScreen)
    }

    private fun onShowNextScreen(needToShow: Boolean) {
        if (needToShow) {
            val email = binding.emailEditText.text.toString().trim()
            (activity as LoginActivity).replaceFragments(
                ChangePasswordFragment.newInstance(email), ChangePasswordFragment.TAG
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ForgotPasswordFragment()
        const val TAG = "ForgotPasswordFragment"
    }
}