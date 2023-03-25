package com.melitopolcherry.hackathon.features.orderpast

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.melitopolcherry.hackathon.adapters.OrdersAdapter
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.models.order.GetOrdersResponse
import com.melitopolcherry.hackathon.data.models.order.OrderItem
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentTicketPastListBinding
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.features.orderdetails.DetailsOrderActivity
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersPastFragment : BaseFragment<FragmentTicketPastListBinding>(),
    OrdersAdapter.OnTicketListItemSelectedListener,
    SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private val viewModel: OrdersPastViewModel by viewModels()
    private var ordersAdapter: OrdersAdapter? = null
    lateinit var layoutManager: LinearLayoutManager

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTicketPastListBinding.inflate(inflater, container, false)

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.ordersResponse.observe(this) {
            if (it?.orders != null && it.orders?.isNotEmpty()!!) {
                setData(it)
            } else {
                if (it?.orders == null) {
                    showUnauthorizedState()
                } else if (viewModel.currentPage == 0) {
                    showEmptyState()
                }
            }
        }
        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ticketsRecyclerView.setHasFixedSize(true)
        binding.closeButton.setOnClickListener(this)
        binding.swipeToRefresh.setOnRefreshListener(this)
        binding.unloginedState.goToLoginButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.putExtra(Enums.BundleCodes.LoginType.name, 111)
            startActivity(intent)
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.closeButton -> {
                (activity as MainActivity).onBackPressed()
            }
        }
    }

    override fun onRefresh() {
        binding.swipeToRefresh.isRefreshing = false
        viewModel.currentPage = 0
        viewModel.canLoadNextPage = true
        viewModel.getOrders()
    }

    fun setData(it: GetOrdersResponse?) {
        if (it?.orders != null && it.orders?.isNotEmpty()!!) {
            if (ordersAdapter == null) {
                ordersAdapter = OrdersAdapter(showTicketsButton = false)
                layoutManager = LinearLayoutManager(requireContext())
                binding.ticketsRecyclerView.layoutManager = layoutManager
                binding.ticketsRecyclerView.addOnScrollListener(scrollListener)
                binding.ticketsRecyclerView.adapter = ordersAdapter
                ordersAdapter?.setOnTicketListItemSelectedListener(this)
            }
            showRecycler()
            if (viewModel.currentPage == 0) {
                ordersAdapter?.setData(it.orders!!)
            } else {
                ordersAdapter?.updateData(it.orders!!)
            }
            if (viewModel.currentPage >= it.totalElements!!) {
                viewModel.canLoadNextPage = false
            } else {
                viewModel.currentPage += 1
                viewModel.canLoadNextPage = true
            }
        } else {
            if (viewModel.currentPage == 0) {
                showEmptyState()
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
                if (viewModel.canLoadNextPage) {
                    if ((layoutManager.childCount + layoutManager.itemCount) >= layoutManager.findFirstVisibleItemPosition()) {
                        viewModel.canLoadNextPage = false
                        println("🚧total Last Item Wow !")
                        viewModel.getOrders()
                    }
                }
            }
        }
    }

    override fun itemSelected(order: OrderItem?) {
        val intent = Intent(requireContext(), DetailsOrderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra(Enums.BundleCodes.OrderId.name, order?.id)
        intent.putExtra(
            Enums.BundleCodes.Point.name,
            LatLng(order?.venue?.latitude!!, order.venue?.longitude!!)
        )
        startActivity(intent)
    }

    override fun showTicketsClicked(order: OrderItem?) {}

    private fun showUnauthorizedState() {
        binding.ticketsRecyclerView.visibility = View.INVISIBLE
        binding.noPastTicketsLayout.root.visibility = View.INVISIBLE
        binding.unloginedState.root.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.ticketsRecyclerView.visibility = View.INVISIBLE
        binding.noPastTicketsLayout.root.visibility = View.VISIBLE
        binding.unloginedState.root.visibility = View.INVISIBLE
    }

    private fun showRecycler() {
        binding.noPastTicketsLayout.root.visibility = View.GONE
        binding.ticketsRecyclerView.visibility = View.VISIBLE
        binding.unloginedState.root.visibility = View.INVISIBLE
    }

    override fun onDetach() {
        ordersAdapter = null
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance() = OrdersPastFragment()
        const val TAG = "OrdersPastFragment"
    }
}