package com.melitopolcherry.hackathon.features.filters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.melitopolcherry.hackathon.adapters.SmallCategoriesAdapter
import com.melitopolcherry.hackathon.adapters.SmallCategoriesAdapter.OnSmallCategorySelectedListener
import com.melitopolcherry.hackathon.core.presentation.BaseFragment
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.livedata.FiltersConfigLiveData
import com.melitopolcherry.hackathon.data.livedata.MarkersLiveData
import com.melitopolcherry.hackathon.data.models.CategoryType
import com.melitopolcherry.hackathon.data.models.SmallCategory
import com.melitopolcherry.hackathon.databinding.FragmentSmallCategoriesBinding
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class SmallCategoriesFragment : BaseFragment<FragmentSmallCategoriesBinding>(), OnSmallCategorySelectedListener,
    Observer<EventsFilters> {

    private var filtersConfigLiveData: FiltersConfigLiveData? = null
    private val markersLiveData = MarkersLiveData.instance
    private var eventCategory = arrayOf<SmallCategory>()
    private var smallCategoriesAdapter: SmallCategoriesAdapter? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSmallCategoriesBinding.inflate(inflater, container, false)

    override fun onCreateView() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filtersConfigLiveData = FiltersConfigLiveData.instance
        filtersConfigLiveData?.getFilters()?.observe(viewLifecycleOwner, this)

        setupViews()
    }

    override fun onChanged(t: EventsFilters?) {
        if (t?.categories != null) {
            updateCategories(t.categories)
        }
    }

    fun setupViews() {
        if (view != null) {

            val arrayTutorialType = object : TypeToken<Array<SmallCategory>>() {}.type

            val arrayOfSmallCategories: Array<SmallCategory> =
                Gson().fromJson(loadJSONFromAsset(), arrayTutorialType)
            eventCategory = arrayOfSmallCategories
            smallCategoriesAdapter =
                SmallCategoriesAdapter(eventCategory.toList(), eventCategory[0], eventCategory.last())
            if (filtersConfigLiveData?.getFilters()?.value != null) {
                val selectedCategories = ArrayList<SmallCategory>()
                val selectedIds = filtersConfigLiveData?.getFilters()?.value?.categories
                if (selectedIds != null && selectedIds.isNotEmpty()) {
                    for (id in 0 until selectedIds.size) {
                        eventCategory.forEach { category ->
                            if (category.value == selectedIds[id].value) {
                                selectedCategories.add(category)
                            }
                        }
                    }
                    selectedIds.clear()
                    smallCategoriesAdapter?.setSelectedCategories(selectedCategories)
                } else {
                    smallCategoriesAdapter?.setSelectedCategories(selectedCategories)
                }
            }
            smallCategoriesAdapter?.setOnSmallCategorySelectedListener(this)

            binding.smallCategoriesRecyclerView.adapter = smallCategoriesAdapter
            binding.smallCategoriesRecyclerView.setHasFixedSize(true)
        }
    }

    private fun loadJSONFromAsset(): String? {
        return try {
            val `is`: InputStream = activity?.assets?.open("categories.json")!!
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    private fun updateCategories(categories: ArrayList<SmallCategory>?) {
        Timber.d(categories.toString())
        val selectedCategories = ArrayList<SmallCategory>()
        if (categories != null && categories.isNotEmpty()) {
            categories.forEachIndexed { _, smallCategory ->
                eventCategory.forEach { category ->
                    if (category.value == smallCategory.value) {
                        selectedCategories.add(category)
                    }
                }
            }
            categories.clear()
            smallCategoriesAdapter?.setSelectedCategories(selectedCategories)
        } else {
            smallCategoriesAdapter?.setSelectedCategories(selectedCategories)
        }
    }

    override fun smallCategorySelected(selectedCategories: ArrayList<SmallCategory>) {
        if (selectedCategories.size > 0) {
            val categoriesArray = ArrayList<SmallCategory>()
            selectedCategories.forEach { category ->
                if (category.categoryType() != CategoryType.ALL) {
                    categoriesArray.add(category)
                }
            }
            smallCategoriesAdapter?.setSelectedCategories(selectedCategories)
            markersLiveData.getEvents().value = selectedCategories.map { it.type!! }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun allButtonClicked() {
        smallCategoriesAdapter?.notifyDataSetChanged()
        markersLiveData.getEvents().value = listOf()

    }

    override fun addButtonClicked() {

    }

    companion object {
        @JvmStatic
        fun newInstance() = SmallCategoriesFragment()

        const val TAG = "SmallCategoriesFragment"
    }
}