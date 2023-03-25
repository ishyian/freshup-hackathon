package com.melitopolcherry.hackathon.features.account

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.models.Address
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentAccountBinding
import com.melitopolcherry.hackathon.features.bottom.UpdatePasswordBottomFragment
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(), View.OnClickListener {

    private val viewModel: AccountViewModel by viewModels()

    private var deviceHeight = 0

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAccountBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.showContent.observe(this) {
            binding.accountContent.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
        viewModel.goToLogin.observe(this) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
        viewModel.userUpdates.observe(this) {
            binding.accountFullName.text =
                getString(R.string.full_name_template, it.firstName, it.secondName)
            binding.accountEmail.text = getString(R.string.email_template, it.email)
            setupAddresses(it.address)
            setupPhoneNumbers(it.phone?.number)
        }
        viewModel.loginMethodUpdates.observe(this) {
            if (it != null && it == Enums.LoginMethods.Email) {
                binding.accountChangePass.visibility = View.VISIBLE
                binding.accountSeparator6.visibility = View.VISIBLE
                binding.subView2.visibility = View.VISIBLE
                binding.accountSeparator5.visibility = View.VISIBLE
                binding.accountSeparatorChangePswd.visibility = View.GONE
            }
        }
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val displayMetrics: DisplayMetrics = activity?.resources?.displayMetrics!!
        deviceHeight = displayMetrics.heightPixels
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            binding.appVersionTextView.text = pInfo.versionName + ":" + pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        viewModel.getUser()

        binding.accountLogoutTextView.setOnClickListener(this)
        binding.accountDeleteTextView.setOnClickListener(this)
        binding.accountRateAppTextView.setOnClickListener(this)
        binding.accountTermsTextView.setOnClickListener(this)
        binding.accountPrivacyTextView.setOnClickListener(this)
        binding.accountSupportTextView.setOnClickListener(this)
        binding.accountChangePass.setOnClickListener(this)
        binding.deliveryInfoLayout.setOnClickListener(this)
        binding.changePhoneLayout.setOnClickListener(this)
        binding.personalLayout.setOnClickListener(this)
        binding.closeButton.setOnClickListener(this)
    }

    private fun setupAddresses(userAddress: Address?) {
        if (userAddress != null) {
            binding.accountAddress.text =
                getString(R.string.address_template, userAddress.streetAddress, userAddress.city)
        } else {
            binding.accountAddress.text = getString(R.string.no_address)
        }
    }

    private fun setupPhoneNumbers(phoneNumber: String?) {
        println("🗿 PHONE$phoneNumber")
        if (phoneNumber != null && phoneNumber.length == 10) {
            val sub1 = phoneNumber.substring(0, 3)
            val sub2 = phoneNumber.substring(3, 6)
            val sub3 = phoneNumber.substring(6, 10)
            val formattedNumber = "+1 ($sub1) $sub2-$sub3"
            binding.accountPhone.text = getString(R.string.phone_number_template, formattedNumber)
        } else if (phoneNumber != null && phoneNumber.length == 17) {
            binding.accountPhone.text = getString(R.string.phone_number_template, phoneNumber)
        } else {
            binding.accountPhone.text = getString(R.string.phone_number)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.accountSupportTextView -> {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "message/rfc822"
                i.putExtra(Intent.EXTRA_EMAIL, arrayOf("tonydarkowork@evenz.co"))
                //                i.putExtra(Intent.EXTRA_SUBJECT, "Issue mail by user(${user?.id})")
                i.putExtra(Intent.EXTRA_TEXT, "Issue is: ")
                try {
                    startActivity(Intent.createChooser(i, "Send mail to support"))
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        requireContext(), "There are no email clients installed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            binding.closeButton -> {
                (activity as MainActivity).onBackPressed()
            }
            binding.accountChangePass -> {
                UpdatePasswordBottomFragment(deviceHeight).show(
                    childFragmentManager,
                    UpdatePasswordBottomFragment.TAG
                )
            }
            binding.accountLogoutTextView -> {
                MaterialDialog(requireContext()).show {
                    title(R.string.account_logout)
                    message(R.string.account_logout_dialog)
                    positiveButton(R.string.yes) {
                        logout()
                    }
                    negativeButton(R.string.no) {
                        cancel()
                    }
                    cancelable(false)
                }
            }
            binding.accountDeleteTextView -> {
                MaterialDialog(requireContext()).show {
                    title(R.string.account_delete)
                    message(R.string.account_delete_dialog)
                    positiveButton(R.string.yes) {
                        deleteUser()
                    }
                    negativeButton(R.string.no) {
                        cancel()
                    }
                    cancelable(false)
                }
            }
            binding.accountRateAppTextView -> {
                launchUri(Uri.parse(requireContext().resources.getString(R.string.google_play_url)))
            }
            binding.accountTermsTextView -> {
                launchUri(Uri.parse(requireContext().resources.getString(R.string.terms_url)))
            }
            binding.accountPrivacyTextView -> {
                launchUri(
                    Uri.parse(requireContext().resources.getString(R.string.privacy_policy_url))
                )
            }
        }
    }

    private fun launchUri(uri: Uri) {
        val showUri = Intent(Intent.ACTION_VIEW, uri)
        showUri.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(showUri)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun logout() {
        if (viewModel.user == null) {
            Toast.makeText(requireContext(), "Logout error. User is null", Toast.LENGTH_SHORT)
                .show()
            return
        }

        println("🗿${viewModel.loginMethod}")

        when (viewModel.loginMethod) {
            Enums.LoginMethods.Facebook -> {
                LoginManager.getInstance().logOut()
                viewModel.deleteFromLocalAndShowLogin()
            }
            Enums.LoginMethods.Google -> {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

                googleSignInClient.signOut().addOnCompleteListener {
                    googleSignInClient.revokeAccess().addOnCompleteListener {
                        viewModel.deleteFromLocalAndShowLogin()
                    }
                }
            }
            Enums.LoginMethods.Email -> {
                viewModel.deleteFromLocalAndShowLogin()
            }
            else -> {
                viewModel.deleteFromLocalAndShowLogin()
            }
        }
    }

    private fun deleteUser() {
        val accessToken = viewModel.accountManager.getToken()
        if (accessToken == null) {
            //NPE can be only in case bad authentication logic
            Toast.makeText(requireContext(), "Logout error. User is null", Toast.LENGTH_SHORT)
                .show()
            return
        }
        viewModel.deleteUser()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AccountFragment()
        const val TAG = "AccountFragment"
    }
}