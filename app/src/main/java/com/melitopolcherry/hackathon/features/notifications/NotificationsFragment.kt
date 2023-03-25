package com.melitopolcherry.hackathon.features.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.NotificationsAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.SwipeToDeleteCallback
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.custom_view.doOnApplyWindowInsets
import com.melitopolcherry.hackathon.data.models.notification.NotificationsItem
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentNotificationsBinding
import com.melitopolcherry.hackathon.features.dialogs.SelectPushDialog
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.features.promocodes.PromocodesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: NotificationsViewModel by viewModels()
    private var notificationsAdapter: NotificationsAdapter? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentNotificationsBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.notificationsResponse.observe(this) {
            if (it != null && it.isNotEmpty()) {
                setupViews(it)
            } else {
                if (it == null) {
                    showUnloginedState()
                } else {
                    showEmptyState()
                }
            }
        }
        viewModel.processError.observe(this) {
            (activity as MainActivity).processError(it) {}
        }
    }

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.notificationsFrame.doOnApplyWindowInsets { view, insets, _ ->
            view.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }
    }

    private fun showLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.putExtra(Enums.BundleCodes.LoginType.name, 111)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notificationsRecyclerView.setHasFixedSize(true)
        enableSwipeToDelete()

        binding.swipeToRefresh.setOnRefreshListener(this)
        binding.unloginedState.goToLoginButton.setOnClickListener {
            showLogin()
        }
        binding.secondActionButton.setOnClickListener {
            if (viewModel.tokenLiveData.value != null) {
                fragmentManager?.commit {
                    replace(
                        R.id.map_home_container,
                        PromocodesFragment.newInstance(),
                        PromocodesFragment.TAG
                    )
                    addToBackStack(PromocodesFragment.TAG)
                }
            } else {
                showLogin()
            }
        }
        binding.toolbarTitle.setOnLongClickListener {
            SelectPushDialog {
                viewModel.getPush(it!!)
            }.show(
                childFragmentManager,
                SelectPushDialog.TAG
            )
            return@setOnLongClickListener false
        }
    }

    override fun onRefresh() {
        viewModel.getNotifications()
        binding.swipeToRefresh.isRefreshing = false
    }

    private fun setupViews(it: List<NotificationsItem>) {
        if (notificationsAdapter == null) {
            notificationsAdapter = NotificationsAdapter()
            binding.notificationsRecyclerView.adapter = notificationsAdapter
        }
        if (binding.notificationsRecyclerView.visibility != View.VISIBLE) {
            binding.noNotificationsLayout.root.visibility = View.GONE
            binding.notificationsRecyclerView.visibility = View.VISIBLE
        }
        if (it != notificationsAdapter?.listOfNotifications) {
            notificationsAdapter?.setData(it)
        }
    }

    private fun showEmptyState() {
        binding.noNotificationsLayout.root.visibility = View.VISIBLE
        binding.notificationsRecyclerView.visibility = View.INVISIBLE
        binding.unloginedState.root.visibility = View.INVISIBLE
    }

    private fun showUnloginedState() {
        binding.noNotificationsLayout.root.visibility = View.INVISIBLE
        binding.notificationsRecyclerView.visibility = View.INVISIBLE
        binding.unloginedState.root.visibility = View.VISIBLE
    }

    private fun enableSwipeToDelete() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = notificationsAdapter?.listOfNotifications?.get(position)

                viewModel.deleteNotification(item?.id!!)
                notificationsAdapter?.listOfNotifications?.removeAt(position)
                notificationsAdapter?.notifyItemRemoved(position)
                if (notificationsAdapter?.listOfNotifications?.size == 0) {
                    showEmptyState()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.notificationsRecyclerView)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationsFragment()

        const val TAG = "NotificationsFragment"
    }
}