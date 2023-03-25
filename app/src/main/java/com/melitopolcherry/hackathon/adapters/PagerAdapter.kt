package com.melitopolcherry.hackathon.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.melitopolcherry.hackathon.features.profile.ProfileSuggestedListFragment
import com.melitopolcherry.hackathon.features.profile.ProfileTrackingListFragment

class PagerAdapter(fm: Fragment) :
    FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                ProfileSuggestedListFragment.newInstance()
            }
            else -> {
                ProfileTrackingListFragment.newInstance()
            }
        }
    }
}