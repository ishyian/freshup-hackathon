package com.melitopolcherry.hackathon.features.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager.widget.ViewPager
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.PagerAdapter
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.custom_view.doOnApplyWindowInsets
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.FragmentProfileBinding
import com.melitopolcherry.hackathon.features.account.AccountFragment
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : BaseFragment<FragmentProfileBinding>(), TabLayout.OnTabSelectedListener,
    ViewPager.OnPageChangeListener {

    private var currentTab = 0
    private var profileFragments = listOf<Fragment>()
    private var pagerAdapter: PagerAdapter? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileBinding.inflate(inflater, container, false)

    override fun onResume() {
        super.onResume()
        if (AuthTokenLiveData.instance.value?.refreshToken == null) {
            showUnauthorizedText()
        } else {
            showUI()
        }
    }

    override fun onCreateView() {
        binding.profileRootLayout.doOnApplyWindowInsets { view, insets, _ ->
            view.updatePadding(
                top = insets.systemWindowInsetTop,
            )
            insets
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.unloginedState.goToLoginButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.putExtra(Enums.BundleCodes.LoginType.name, 111)
            startActivity(intent)
        }
        setupTabs()
    }

    private fun setupTabs() {
        pagerAdapter = PagerAdapter(this)
        profileFragments = getPages()
        binding.profileViewPager.offscreenPageLimit = profileFragments.size - 1
        binding.profileViewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.profileTabLayout, binding.profileViewPager) { tab, position ->
            tab.text = if (position == 0) {
                getString(R.string.profile_tab_suggested)
            } else {
                getString(R.string.profile_tab_tracking)
            }
        }.attach()

        binding.secondActionButton.setOnClickListener {
            if (AuthTokenLiveData.instance.value != null) {
                fragmentManager?.commit {
                    replace(
                        R.id.map_home_container,
                        AccountFragment.newInstance(),
                        AccountFragment.TAG
                    )
                    addToBackStack(AccountFragment.TAG)
                }
            } else {
                showLogin()
            }
        }
        binding.profileTabLayout.addOnTabSelectedListener(this)

        setupCurrentTab(currentTab)
    }

    private fun showLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.putExtra(Enums.BundleCodes.LoginType.name, 111)
        startActivity(intent)
    }

    private fun getPages(): List<Fragment> {
        return listOf(
            ProfileSuggestedListFragment.newInstance(),
            ProfileTrackingListFragment.newInstance()
        )
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (binding.profileTabLayout.selectedTabPosition) {
            in 0..1 -> {
                setupCurrentTab(binding.profileTabLayout.selectedTabPosition)
            }
        }
    }

    private fun setupCurrentTab(position: Int) {
        currentTab = position
        binding.profileViewPager.setCurrentItem(currentTab, true)
    }

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        binding.profileTabLayout.getTabAt(position)?.select()
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    private fun showUnauthorizedText() {
        binding.profileViewPager.visibility = View.INVISIBLE
        binding.profileTabLayout.visibility = View.INVISIBLE
        binding.unloginedState.root.visibility = View.VISIBLE
    }

    private fun showUI() {
        binding.profileViewPager.visibility = View.VISIBLE
        binding.profileTabLayout.visibility = View.VISIBLE
        binding.unloginedState.root.visibility = View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()

        const val TAG = "ProfileFragment"
    }
}