package com.melitopolcherry.hackathon.features.login.email

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.base.AuthFragment
import com.melitopolcherry.hackathon.custom_view.showInfoDialog
import com.melitopolcherry.hackathon.databinding.FragmentEmailLoginBinding
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.login.forgot_password.ForgotPasswordFragment
import com.melitopolcherry.hackathon.features.login.sign_up.SignUpFragment
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailLoginFragment : AuthFragment<FragmentEmailLoginBinding>(), View.OnClickListener,
    TextView.OnEditorActionListener {

    private val viewModel: EmailLoginViewModel by viewModels()
    private val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEmailLoginBinding {
        return FragmentEmailLoginBinding.inflate(inflater, container, false)
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        initFacebookLogin()
        setupClickListener(
            loginForgotPasswordButton,
            googleSigninButton,
            facebookSignInButton,
            loginEmailNextButton,
            backButton.root,
            passwordEditText,
            loginTermsTextView
        )
        initObservers()
    }

    override fun onClick(v: View?) {
        if (!blockButtons) {
            blockButtons = true
            when (v) {
                binding.loginTermsTextView -> {
                    activity?.supportFragmentManager?.commit {
                        replace(
                            R.id.content_login,
                            SignUpFragment.newInstance(),
                            SignUpFragment.TAG
                        )
                        addToBackStack(SignUpFragment.TAG)
                    }
                }
                binding.backButton.root -> {
                    (activity as LoginActivity).onBackPressed()
                }
                binding.googleSigninButton -> {
                    signIn()
                }
                binding.loginEmailNextButton -> {
                    val email = binding.emailEditText.text.toString().trim()
                    val password = binding.passwordEditText.text.toString().trim()
                    viewModel.loginWithEmail(email, password, LoginActivity.fcmToken)
                }
                binding.loginForgotPasswordButton -> {
                    (activity as LoginActivity).replaceFragments(
                        ForgotPasswordFragment.newInstance(),
                        ForgotPasswordFragment.TAG
                    )
                }
                binding.facebookSignInButton -> {
                    viewModel.isLoading.postValue(true)
                    LoginManager.getInstance().logInWithReadPermissions(
                        this,
                        callbackManager,
                        listOf("public_profile", "email")
                    )
                }
            }
            runDelayedUnblock()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            viewModel.loginWithEmail(email, password, LoginActivity.fcmToken)
            return true
        }
        return false
    }

    private fun initObservers() = with(viewModel) {
        observe(showErrorMessage, ::showInfoDialog)
        observe(processError, ::processAuthError)
        observe(goToMain, ::onNavigateToMainScreen)
    }

    private fun onNavigateToMainScreen(ignore: Boolean) {
        if ((activity as LoginActivity).loginType != 111) {
            val intent =
                Intent(requireContext(), MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        activity?.finish()
    }

    private fun initFacebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, viewModel)
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
        fun newInstance() = EmailLoginFragment()
        const val TAG = "LoginFragment"
    }
}