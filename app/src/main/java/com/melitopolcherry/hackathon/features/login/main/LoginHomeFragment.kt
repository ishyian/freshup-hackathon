package com.melitopolcherry.hackathon.features.login.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.base.AuthFragment
import com.melitopolcherry.hackathon.custom_view.showInfoDialog
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentLoginHomeBinding
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.login.email.EmailLoginFragment
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
class LoginHomeFragment : AuthFragment<FragmentLoginHomeBinding>(), View.OnClickListener {

    private val viewModel: LoginHomeViewModel by viewModels()

    private val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    private var animatedDrawable: AnimationDrawable? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLoginHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        lifecycleOwner = viewLifecycleOwner
        viewModel = viewModel

        setupClickListener(
            loginButton,
            googleSigninButton,
            facebookSignInButton,
            signUpButton,
            skipRegButton
        )

        initObservers()
        initFacebookLogin()

        (activity as LoginActivity).loginType =
            (activity as LoginActivity).intent?.extras?.getInt(Enums.BundleCodes.LoginType.name)
    }

    override fun onResume() {
        super.onResume()
        initBackground()
    }

    override fun onStop() {
        super.onStop()
        animatedDrawable?.stop()
    }

    override fun onClick(v: View?) {
        if (!blockButtons) {
            blockButtons = true
            when (v) {
                binding.googleSigninButton -> {
                    signIn()
                }
                binding.loginButton -> {
                    activity?.supportFragmentManager?.commit {
                        replace(
                            R.id.content_login, EmailLoginFragment.newInstance(),
                            EmailLoginFragment.TAG
                        )
                        addToBackStack(EmailLoginFragment.TAG)
                    }
                }
                binding.signUpButton -> {
                    activity?.supportFragmentManager?.commit {
                        replace(
                            R.id.content_login, SignUpFragment.newInstance(),
                            SignUpFragment.TAG
                        )
                        addToBackStack(SignUpFragment.TAG)
                    }
                }
                binding.skipRegButton -> {
                    viewModel.saveSkipLogin()
                    if ((activity as LoginActivity).loginType != 111) {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        startActivity(intent)
                    }
                    activity?.finish()
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

    private fun initObservers() = with(viewModel) {
        observe(showErrorMessage, ::showInfoDialog)
        observe(processError, ::processAuthError)
        observe(navigateToMain, ::onNavigateToMainScreen)
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

    private fun initBackground() {
        binding.backImage.setBackgroundResource(R.drawable.login_background)
        animatedDrawable = binding.backImage.background as AnimationDrawable
        animatedDrawable?.start()
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

    private val getGoogleDataBack = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
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
                            authCode!!
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
        fun newInstance() = LoginHomeFragment()
        const val TAG = "LoginHomeFragment"
    }
}