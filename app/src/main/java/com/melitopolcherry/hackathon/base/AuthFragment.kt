package com.melitopolcherry.hackathon.base

import android.os.Handler
import android.os.Looper
import androidx.viewbinding.ViewBinding
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.custom_view.showInfoDialog
import com.melitopolcherry.hackathon.data.restApi.errorsHandler.ApiException
import com.melitopolcherry.hackathon.data.restApi.errorsHandler.ErrorHandler
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

abstract class AuthFragment<B : ViewBinding> : BaseFragment<B>() {

    var blockButtons = false

    protected fun processAuthError(throwable: Throwable) {
        throwable.printStackTrace()
        if (throwable is UnknownHostException) {
            showInfoDialog("No internet connection")
        } else if (throwable is SocketTimeoutException || throwable is TimeoutException) {
            //  showInfoDialog("Error1", "No connection")
        } else if (throwable is HttpException) {
            if (throwable.code() == 404 || throwable.code() == 403 || throwable.code() == 400) {
                val message = ErrorHandler().processError(throwable).localizedMessage
                message?.let {
                    showInfoDialog(it)
                }
            } else if (throwable is ApiException) {
                var message = throwable.details.message
                if (message == null) {
                    message = throwable.message
                }
                if (message != null) {
                    showInfoDialog(message)
                }
            } else {
                showInfoDialog("Incorrect response")
            }
        } else {
            throwable.message?.let {
                showInfoDialog(it)
            }
        }
    }

    protected fun runDelayedUnblock() {
        Handler(Looper.getMainLooper()).postDelayed({ blockButtons = false }, DELAY_IN_MS)
    }

    companion object {
        const val DELAY_IN_MS: Long = 900
    }
}