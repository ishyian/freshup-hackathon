package com.melitopolcherry.hackathon.features.promocodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.adapters.PromocodesAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.SwipeToDeleteCallback
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.models.responces.PromoCode
import com.melitopolcherry.hackathon.databinding.FragmentPromocodesBinding
import com.melitopolcherry.hackathon.features.bottom.AddPromocodeBottomFragment
import com.melitopolcherry.hackathon.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromocodesFragment : BaseFragment<FragmentPromocodesBinding>(), View.OnClickListener {

    private val viewModel: PromocodesViewModel by viewModels()

    private var promocodesAdapter: PromocodesAdapter? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPromocodesBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.showEmptyState.observe(this) {
            if (it) {
                showEmptyState()
            } else {
                showRecycler()
            }
        }
        viewModel.setData.observe(this) {
            setData(it)
        }
        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.promosRecyclerView.setHasFixedSize(true)
        binding.closeButton.setOnClickListener(this)
        binding.addPromoButton.setOnClickListener(this)

        enableSwipeToDelete()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.closeButton -> {
                (activity as MainActivity).onBackPressed()
            }
            binding.addPromoButton -> {
                AddPromocodeBottomFragment(viewModel.addPromocodeCallback).show(
                    childFragmentManager,
                    AddPromocodeBottomFragment.TAG
                )
            }
        }
    }

    fun setData(listOfCodes: List<PromoCode>) {
        if (listOfCodes.isNotEmpty()) {
            if (promocodesAdapter == null || binding.promosRecyclerView.adapter == null) {
                promocodesAdapter = PromocodesAdapter()
                binding.promosRecyclerView.adapter = promocodesAdapter
            }
            showRecycler()
            promocodesAdapter?.setData(listOfCodes)
        } else {
            showEmptyState()
        }
    }

    private fun enableSwipeToDelete() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(
                @NonNull
                viewHolder: RecyclerView.ViewHolder, i: Int
            ) {
                val position = viewHolder.bindingAdapterPosition
                val item = promocodesAdapter?.listOfPromocodes?.get(position)
                item?.let {
                    viewModel.deletePromo(it)
                    promocodesAdapter?.listOfPromocodes?.removeAt(position)
                    promocodesAdapter?.notifyItemRemoved(position)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.promosRecyclerView)
    }

    private fun showEmptyState() {
        binding.promosRecyclerView.visibility = View.INVISIBLE
        binding.emptyStateLayout.visibility = View.VISIBLE
    }

    private fun showRecycler() {
        binding.emptyStateLayout.visibility = View.GONE
        binding.promosRecyclerView.visibility = View.VISIBLE
    }

    override fun onDetach() {
        promocodesAdapter = null
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance() = PromocodesFragment()

        const val TAG = "PromocodesFragment"
    }
}