package com.melitopolcherry.hackathon.features.ordersupcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.OrdersAdapter
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.custom_view.doOnApplyWindowInsets
import com.melitopolcherry.hackathon.data.models.order.GetOrdersResponse
import com.melitopolcherry.hackathon.data.models.order.OrderItem
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentTicketUpcommingListBinding
import com.melitopolcherry.hackathon.features.bottom.OrderLinksBottomFragment
import com.melitopolcherry.hackathon.features.dialogs.PdfPreviewDialog
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.features.orderdetails.DetailsOrderActivity
import com.melitopolcherry.hackathon.features.orderpast.OrdersPastFragment
import com.melitopolcherry.hackathon.features.promocodes.PromocodesFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersUpcomingFragment : BaseFragment<FragmentTicketUpcommingListBinding>(),
    OrdersAdapter.OnTicketListItemSelectedListener,
    SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: OrdersUpcomingViewModel by viewModels()

    private var ordersAdapter: OrdersAdapter? = null
    lateinit var layoutManager: LinearLayoutManager

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTicketUpcommingListBinding.inflate(inflater, container, false)

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

    override fun onCreateView() {
        binding.viewModel = viewModel
        binding.upcomingMain.doOnApplyWindowInsets { view, insets, _ ->
            view.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ticketsRecyclerView.setHasFixedSize(true)
        binding.swipeToRefresh.setOnRefreshListener(this)
        binding.unloginedState.goToLoginButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.putExtra(Enums.BundleCodes.LoginType.name, 111)
            startActivity(intent)
        }
        binding.secondActionButton.setOnClickListener {
            if (viewModel.tokenLiveData.value != null) {
                fragmentManager?.commit {
                    replace(
                        R.id.map_home_container,
                        OrdersPastFragment.newInstance(),
                        OrdersPastFragment.TAG
                    )
                    addToBackStack(PromocodesFragment.TAG)
                }
            } else {
                showLogin()
            }
        }
    }

    private fun showLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.putExtra(Enums.BundleCodes.LoginType.name, 111)
        startActivity(intent)
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
                ordersAdapter = OrdersAdapter(showTicketsButton = true)
                layoutManager = LinearLayoutManager(requireContext())
                binding.ticketsRecyclerView.layoutManager = layoutManager
                binding.ticketsRecyclerView.addOnScrollListener(scrollListener)
                ordersAdapter?.setOnTicketListItemSelectedListener(this)
                binding.ticketsRecyclerView.adapter = ordersAdapter
            }
            showRecycler()
            if (viewModel.currentPage == 0) {
                if (it.orders != ordersAdapter?.listOfOrders) {
                    ordersAdapter?.setData(it.orders!!)
                }
            } else {
                ordersAdapter?.updateData(it.orders!!)
            }
            if (viewModel.currentPage >= it.totalPages!!) {
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

    override fun showTicketsClicked(order: OrderItem?) {
        if (order?.tickets != null && order.tickets?.isNotEmpty()!!) {
            PdfPreviewDialog(order.tickets?.get(0)!!).show(
                childFragmentManager,
                "PdfPreviewDialog"
            )
        } else if (order?.ticketUrls != null && order.ticketUrls?.isNotEmpty()!!) {
            OrderLinksBottomFragment(order.ticketUrls!!).show(
                childFragmentManager,
                OrderLinksBottomFragment.TAG
            )
            //            try {
            //                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(order.ticketUrls?.get(0))))
            //            } catch (e: Throwable) {
            //                e.printStackTrace()
            //            }
        } else {
            Toast.makeText(
                requireContext(),
                "Your ticket currently is unavailable",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showUnauthorizedState() {
        binding.ticketsRecyclerView.visibility = View.INVISIBLE
        binding.noUpcomingTicketsLayout.root.visibility = View.INVISIBLE
        binding.unloginedState.root.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.ticketsRecyclerView.visibility = View.INVISIBLE
        binding.noUpcomingTicketsLayout.root.visibility = View.VISIBLE
        binding.unloginedState.root.visibility = View.INVISIBLE
    }

    private fun showRecycler() {
        binding.noUpcomingTicketsLayout.root.visibility = View.GONE
        binding.ticketsRecyclerView.visibility = View.VISIBLE
        binding.unloginedState.root.visibility = View.INVISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance() = OrdersUpcomingFragment()

        const val TAG = "OrdersUpcomingFragment"
    }
}