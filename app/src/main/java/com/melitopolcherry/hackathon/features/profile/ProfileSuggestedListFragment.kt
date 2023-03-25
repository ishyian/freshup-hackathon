package com.melitopolcherry.hackathon.features.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.SuggestedEventsAdapter
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.custom_view.GridSpacingItemDecoration
import com.melitopolcherry.hackathon.data.models.suggested.GetSuggestedResponse
import com.melitopolcherry.hackathon.data.models.suggested.SuggestedItem
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentProfileSuggestedListBinding
import com.melitopolcherry.hackathon.features.eventdetails.activity.DetailsEventActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSuggestedListFragment : BaseFragment<FragmentProfileSuggestedListBinding>(),
    SwipeRefreshLayout.OnRefreshListener,
    SuggestedEventsAdapter.OnSuggestedSelected {

    private val viewModel: ProfileSuggestedViewModel by viewModels()

    private var suggestedAdapter: SuggestedEventsAdapter? = null
    lateinit var layoutManager: GridLayoutManager

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileSuggestedListBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.suggestedResponse.observe(this) {
            if (it?.items != null && it.items?.isNotEmpty()!!) {
                setData(it)
            } else {
                showNoEventsText()
            }
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
        binding.swipeToRefresh.setOnRefreshListener(this)
        binding.profileRecyclerView.setHasFixedSize(true)
        binding.profileRecyclerView.setItemViewCacheSize(50)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
                if (viewModel.canLoad) {
                    if ((layoutManager.childCount + layoutManager.itemCount) >= layoutManager.findFirstVisibleItemPosition()) {
                        viewModel.canLoad = false
                        println("🚧total Last Item Wow !")
                        viewModel.getSuggested()
                    }
                }
            }
        }
    }

    private fun setData(suggestedResponse: GetSuggestedResponse?) {
        if (suggestedResponse?.items != null && suggestedResponse.items?.isNotEmpty()!!) {
            if (suggestedAdapter == null) {
                suggestedAdapter = SuggestedEventsAdapter(this)
                layoutManager = GridLayoutManager(requireContext(), 2)
                binding.profileRecyclerView.layoutManager = layoutManager
                binding.profileRecyclerView.adapter = suggestedAdapter
                binding.profileRecyclerView.addOnScrollListener(scrollListener)
                binding.profileRecyclerView.addItemDecoration(
                    GridSpacingItemDecoration(
                        2,
                        resources.getDimension(R.dimen.padding_16).toInt(),
                        true
                    )
                )
            }
            if (viewModel.page == 0) {
                if (suggestedResponse.items != suggestedAdapter?.listOfSuggested) {
                    suggestedAdapter?.setData(suggestedResponse.items!!)
                }
            } else {
                suggestedAdapter?.updateData(suggestedResponse.items!!)
            }
            if (viewModel.size > suggestedResponse.totalItems!!) {
                viewModel.canLoad = false
            } else {
                viewModel.page += 1
                viewModel.size += 20
                viewModel.canLoad = true
            }
            showRecycler()
        } else {
            if (viewModel.page == 0) {
                showNoEventsText()
            }
        }
    }

    override fun onSuggestedSelected(suggestedItem: SuggestedItem) {
        val intent = Intent(requireContext(), DetailsEventActivity::class.java)
        intent.putExtra(Enums.BundleCodes.EventId.name, suggestedItem.id)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onRefresh() {
        binding.swipeToRefresh.isRefreshing = false
        viewModel.page = 0
        viewModel.canLoad = true
        viewModel.getSuggested()
    }

    private fun showNoEventsText() {
        binding.noSuggestedLayout.root.visibility = View.VISIBLE
        binding.profileRecyclerView.visibility = View.INVISIBLE
    }

    private fun showRecycler() {
        binding.noSuggestedLayout.root.visibility = View.GONE
        binding.profileRecyclerView.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileSuggestedListFragment()

        const val TAG = "ProfileSuggestedListFragment"
    }
}