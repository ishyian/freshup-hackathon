package com.melitopolcherry.hackathon.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import coil.load
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.databinding.ItemOnboardingBinding

class OnBoardingPagerAdapter : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding =
            ItemOnboardingBinding.inflate(LayoutInflater.from(container.context), container, false)
        fillViews(binding, position)
        container.addView(binding.root, 0)
        return binding.root
    }

    private fun fillViews(binding: ItemOnboardingBinding, position: Int) {
        var resID = 0
        when (position) {
            0 -> {
                resID = R.drawable.illustration_onboarding_1
                binding.onboardingText.text =
                    binding.root.context.getString(R.string.on_boarding_title_1)
            }
            1 -> {
                resID = R.drawable.illustration_onboarding_2
                binding.onboardingText.text =
                    binding.root.context.getString(R.string.on_boarding_title_2)
            }
            2 -> {
                resID = R.drawable.illustration_onboarding_3
                binding.onboardingText.text =
                    binding.root.context.getString(R.string.on_boarding_title_3)
            }
            3 -> {
                resID = R.drawable.illustration_onboarding_4
                binding.onboardingText.text =
                    binding.root.context.getString(R.string.on_boarding_title_4)
            }
        }
        binding.onboardingImage.load(resID){
            bitmapConfig(Bitmap.Config.RGB_565)
            error(R.drawable.placeholder_event)
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}