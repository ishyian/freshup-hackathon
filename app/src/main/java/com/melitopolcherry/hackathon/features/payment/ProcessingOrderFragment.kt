package com.melitopolcherry.hackathon.features.payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentProcessingOrderBinding
import com.melitopolcherry.hackathon.features.main.MainActivity

class ProcessingOrderFragment : BaseFragment<FragmentProcessingOrderBinding>() {

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProcessingOrderBinding.inflate(inflater, container, false)

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.goToOrdersButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra(Enums.BundleCodes.PaymentMove.name, 2)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProcessingOrderFragment()
    }
}