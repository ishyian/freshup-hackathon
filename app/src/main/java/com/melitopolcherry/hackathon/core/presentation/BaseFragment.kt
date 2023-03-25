package com.melitopolcherry.hackathon.core.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding

/**
 * Base Fragment class with helper methods for handling views and back button events.
 *
 * @see Fragment
 */
abstract class BaseFragment<B : ViewBinding> : Fragment(), View.OnClickListener {

    abstract fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): B

    private var _binding: B? = null
    val binding: B get() = _binding ?: error("binding exception")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = initBinding(inflater, container)
        onCreateView()
        return binding.root
    }

    protected open fun onCreateView() = Unit

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onClick(v: View?) = Unit

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

    protected fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
