package com.melitopolcherry.hackathon.features.login.sign_up

import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.base.AuthFragment
import com.melitopolcherry.hackathon.custom_view.showInfoDialog
import com.melitopolcherry.hackathon.databinding.FragmentSignUpBinding
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.login.sign_up_details.SignUpDetailsFragment
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : AuthFragment<FragmentSignUpBinding>(), View.OnClickListener,
    TextView.OnEditorActionListener {

    private val viewModel: SignUpViewModel by viewModels()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isLoading.observe(this) {
            binding.loginEmailNextButton.isClickable = !it
        }
        viewModel.showErrorMessage.observe(this) {
            showInfoDialog(it)
        }
        viewModel.processError.observe(this) {
            processAuthError(it)
        }
        viewModel.showNextScreen.observe(this) {
            if (it) {
                val email = binding.emailEditText.text.toString().trim()
                (activity as LoginActivity).replaceFragments(
                    SignUpDetailsFragment.newInstance(email), SignUpDetailsFragment.TAG
                )
            }
        }
        viewModel.goToMain.observe(this) {
            if ((activity as LoginActivity).loginType != 111) {
                val intent =
                    Intent(requireContext(), MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            activity?.finish()
        }
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginTermsTextView.setOnClickListener(this)
        binding.googleSigninButton.setOnClickListener(this)
        binding.loginEmailNextButton.setOnClickListener(this)
        binding.backButton.root.setOnClickListener(this)
        binding.emailEditText.setOnEditorActionListener(this)
    }

    override fun onClick(v: View?) {
        if (!blockButtons) {
            blockButtons = true
            when (v) {
                binding.backButton.root -> {
                    activity?.onBackPressed()
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
                binding.googleSigninButton -> {
                    signIn()
                }
                binding.loginEmailNextButton -> {
                    val email = binding.emailEditText.text.toString().trim()
                    viewModel.sendEmailForSignUp(email)
                }
            }
            runDelayedUnblock()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            val email = binding.emailEditText.text.toString().trim()
            viewModel.sendEmailForSignUp(email)
            return true
        }
        return false
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.EMAIL))
            .requestServerAuthCode(getString(R.string.default_web_client_id))
            .requestEmail().build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        googleSignInClient.signOut()
        getGoogleDataBack.launch(googleSignInClient.signInIntent)
        viewModel.isLoading.postValue(true)
    }

    private val getGoogleDataBack = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val result = task.getResult(ApiException::class.java)
                if (result != null) {
                    try {
                        val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                            .getResult(ApiException::class.java)
                        val authCode = account?.serverAuthCode
                        viewModel.isLoading.postValue(false)
                        viewModel.googleLoginEvenz(
                            getString(R.string.grant_type),
                            getString(R.string.default_web_client_id),
                            getString(R.string.client_secret),
                            "",
                            authCode!!,
                            LoginActivity.fcmToken
                        )
                    } catch (e: ApiException) {
                        viewModel.isLoading.postValue(false)
                        e.printStackTrace()
                        processAuthError(e)
                    }
                } else {
                    viewModel.isLoading.postValue(false)
                    processAuthError(Throwable("ACCOUNT IS NULL:"))
                }
            } catch (e: Exception) {
                viewModel.isLoading.postValue(false)
                processAuthError(e)
            }
        } else {
            viewModel.isLoading.postValue(false)
            processAuthError(Throwable("RESULT ISN'T OKAY"))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
        const val TAG = "SignUpFragment"
    }
}