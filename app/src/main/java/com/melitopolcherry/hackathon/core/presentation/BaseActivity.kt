package com.melitopolcherry.hackathon.core.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.melitopolcherry.hackathon.base.BaseActivityViewModel
import com.melitopolcherry.hackathon.custom_view.showInfoDialog
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.restApi.errorsHandler.ApiException
import com.melitopolcherry.hackathon.data.restApi.errorsHandler.ErrorHandler
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity(), View.OnClickListener {

    private var _binding: B? = null
    val binding: B get() = _binding ?: error("binding exception")

    private lateinit var baseViewModel: BaseActivityViewModel

    private var exitDialog: MaterialDialog? = null
    var blockButtons: Boolean? = false

    override fun onStart() {
        super.onStart()
        baseViewModel = ViewModelProvider(this).get(BaseActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = initBinding()
        setContentView(binding.root)
        onCreate()
    }

    abstract fun initBinding(): B

    override fun onResume() {
        super.onResume()
        if (AuthTokenLiveData.instance.value == null) {
            val token = baseViewModel.accountManager.getToken()
            if (token != null) {
                AuthTokenLiveData.instance.setToken(token)
            }
        }
    }

    override fun onClick(v: View?) = Unit

    protected open fun onCreate() = Unit

    protected fun setupClickListener(vararg clickIds: View) {
        clickIds.forEach { view ->
            view.setOnClickListener(this)
        }
    }

    protected inline fun <T> LifecycleOwner.observe(
        liveData: LiveData<T>,
        crossinline action: (t: T) -> Unit
    ) {
        liveData.observe(this) {
            it?.let { t ->
                action(t)
            }
        }
    }

    fun processError(throwable: Throwable, repeatRequest: (() -> Unit)?) {
        throwable.printStackTrace()
        println("\n\n🥛 $throwable || ")
        if (throwable is UnknownHostException) {
            //    showInfoDialog("No internet connection")
        } else if (throwable is SocketTimeoutException || throwable is TimeoutException) {
            repeatRequest?.invoke()
            //  showInfoDialog("Error1", "No connection")
        } else if (throwable is HttpException) {
            println("\n\n🥛 1 $throwable || ")
            if (throwable.code() == 404 || throwable.code() == 403 || throwable.code() == 400) {
                if (throwable is ApiException) {
                    println("\n\n🥛 ${throwable.details.code} || ${throwable.details.message}")
                    val code = throwable.details.code
                    var message = throwable.details.message
                    if (code != null) {
                        message = code.description
                    }
                    if (message == null) {
                        message = throwable.message
                    }
                    if (message != null) {
                        showInfoDialog(message)
                    }
                } else {
                    val message = ErrorHandler().processError(throwable).localizedMessage
                    if (message != null) {
                        showInfoDialog(message)
                    }
                }
            } else if (throwable.code() == 401) {
                println("\n\n🥛 2 $throwable || ")
                throwable.printStackTrace()
            } else if (throwable is ApiException) {
                println("\n\n🥛 3 $throwable || ")
                val code = throwable.details.code
                var message = throwable.details.message
                if (code != null) {
                    message = code.description
                }
                if (message == null) {
                    message = throwable.message
                }
                if (message != null) {
                    showInfoDialog(message)
                }
            } else {
                showInfoDialog("Incorrect response")
            }
        } else if (throwable.cause is HttpException) {
            println("\n\n🥛 4 $throwable ||  ")
            (throwable.cause as HttpException).let { it ->
                println("\n\n🥛 4.1 ${throwable.message} ||  ")
                if (it.code() == 401 && (throwable.message?.contains("expired")!! || throwable.message == "We couldn't process your account registration. Please try again.") && exitDialog == null) {
                    println("\n\n🥛 4.11 ${throwable.message} ||  ")
                    if (AuthTokenLiveData.instance.value?.refreshToken != null) {
                        baseViewModel.registrationManager.refreshToken(AuthTokenLiveData.instance.value?.refreshToken!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ accIt ->
                                           AuthTokenLiveData.instance.setToken(accIt)
                                           baseViewModel.accountManager.saveToken(accIt)
                                           repeatRequest?.invoke()

                                           FirebaseMessaging.getInstance().token.addOnSuccessListener {
                                               println("🤢 FCM Token: $it")
                                               baseViewModel.accountManager.updateFCM(
                                                   AuthTokenLiveData.instance.value?.refreshToken!!,
                                                   it
                                               )
                                                   .subscribe({}, { e ->
                                                       e.printStackTrace()
                                                   })
                                           }
                                       }, { e1 ->
                                           e1.printStackTrace()
                                           if (exitDialog == null) {
                                               exitDialog = MaterialDialog(this).show {
                                                   message(text = "You are not authorized, please login again.")
                                                   positiveButton(text = "Ok") {
                                                       AuthTokenLiveData.instance.clearToken()
                                                       baseViewModel.accountManager.deleteFully()
                                                       dismiss()
                                                       goToLogin()
                                                   }
                                                   cancelable(false)
                                               }
                                           }
                                       })
                    } else {
                        goToLogin()
                    }
                } else {
                    println("\n\n🥛 5 $throwable || ")
                    if (it.code() == 401 && throwable.message?.contains("Cannot convert access token to JSON")!! && exitDialog == null) {
                        exitDialog = MaterialDialog(this).show {
                            message(text = "You are not authorized, please login again.")
                            positiveButton(text = "Ok") {
                                AuthTokenLiveData.instance.clearToken()
                                baseViewModel.accountManager.deleteFully()
                                dismiss()
                                goToLogin()
                            }
                            cancelable(false)
                        }
                    } else if (throwable.message?.contains("Cannot convert access token to JSON")!! && exitDialog != null) {
                        println("\n\n🥛 6 $throwable || ")
                        return
                    } else {
                        println("\n\n🥛 7 $throwable || ")
                        throwable.message?.let {
                            showInfoDialog(it)
                        }
                    }
                }
            }
        } else {
            throwable.message?.let {
                showInfoDialog(it)
            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        finish()
    }

    fun runDelayedUnblock() {
        Handler(Looper.getMainLooper()).postDelayed({
                                                        blockButtons = false
                                                    }, DELAY_IN_MS)
    }

    override fun onPause() {
        exitDialog?.dismiss()
        super.onPause()
    }

    override fun onDestroy() {
        exitDialog = null
        blockButtons = null
        super.onDestroy()
    }

    companion object {
        const val DELAY_IN_MS: Long = 500
    }
}