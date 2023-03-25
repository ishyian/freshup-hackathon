package com.melitopolcherry.hackathon.features.login.sign_up_details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import com.melitopolcherry.hackathon.databinding.FragmentSignUpDetailsBinding
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpDetailsFragment : AuthFragment<FragmentSignUpDetailsBinding>(),
    View.OnClickListener, TextView.OnEditorActionListener {

    private val viewModel: SignUpDetailsViewModel by viewModels()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSignUpDetailsBinding {
        return FragmentSignUpDetailsBinding.inflate(inflater, container, false)
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

        binding.signUpButton.setOnClickListener(this)
        binding.resendTimerButton.setOnClickListener(this)
        binding.loginTermsTextView.setOnClickListener(this)
        binding.backButton.root.setOnClickListener(this)
        binding.passwordEditText.setOnEditorActionListener(this)
        binding.emailTextView.text = arguments?.getString("email")

        binding.resendTimerButton.isClickable = false
    }

    override fun onClick(v: View?) {
        if (!blockButtons) {
            blockButtons = true
            when (v) {
                binding.backButton.root -> {
                    fragmentManager?.popBackStack()
                }
                binding.loginTermsTextView -> {
                    val uri = Uri.parse(requireContext().resources.getString(R.string.terms_url))
                    val showTerms = Intent(Intent.ACTION_VIEW, uri)
                    showTerms.addFlags(
                        Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    )
                    try {
                        startActivity(showTerms)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                }
                binding.signUpButton -> {
                    val email = binding.emailTextView.text.toString()
                    val password = binding.passwordEditText.text?.trim().toString()
                    val firstName = binding.firstNameEditText.text?.trim().toString()
                    val lastName = binding.lastNameEditText.text?.trim().toString()
                    val code = binding.codeEditText.text?.trim().toString()

                    viewModel.signUp(email, password, firstName, lastName, code)
                }
                binding.resendTimerButton -> {
                    viewModel.resetTimer(binding.emailTextView.text.toString())
                }
            }
            runDelayedUnblock()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            val email = binding.emailTextView.text.toString()
            val password = binding.passwordEditText.text?.trim().toString()
            val firstName = binding.firstNameEditText.text?.trim().toString()
            val lastName = binding.lastNameEditText.text?.trim().toString()
            val code = binding.codeEditText.text?.trim().toString()
            viewModel.signUp(email, password, firstName, lastName, code)
            return true
        }
        return false
    }

    companion object {
        @JvmStatic
        fun newInstance(email: String) = SignUpDetailsFragment().apply {
            arguments = Bundle().apply {
                putString("email", email)
            }
        }

        const val TAG = "SignUpDetailsFragment"
    }
}