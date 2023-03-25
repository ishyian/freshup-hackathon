package com.melitopolcherry.hackathon.adapters.cities_section

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.databinding.ViewCityHeaderBinding

class CitiesAdapter(private val sectionedItemCallback: SectionedItemCallback.Callback) :
    RecyclerView.Adapter<CitiesAdapter.CitiesHeaderViewHolder?>() {

    private var sourceList: List<CitiesSection>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesHeaderViewHolder {
        val binding =
            ViewCityHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitiesHeaderViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: CitiesSection) {
        sourceList = listOf(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear(){
        sourceList = listOf()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(section: CitiesSection) {
        sourceList = if (sourceList != null && sourceList?.isNotEmpty()!!) {
            listOf(sourceList?.get(0)!!, section)
        } else {
            listOf(section)
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(itemHolder: CitiesHeaderViewHolder, position: Int) =
        itemHolder.bind(sourceList?.get(itemHolder.bindingAdapterPosition)!!)

    override fun getItemCount(): Int {
        return if (sourceList?.size != null) {
            sourceList?.size!!
        } else {
            0
        }
    }

    inner class CitiesHeaderViewHolder(var binding: ViewCityHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CitiesSection) {
            if (item.header.isNotEmpty()) {
                binding.cityHeaderTextView.text = item.header
                binding.cityHeaderTextView.visibility = View.VISIBLE
            } else {
                binding.cityHeaderTextView.visibility = View.GONE
            }

            binding.rvHeadline.adapter = CitiesItemsAdapter(
                item.citiesList,
                item.searchText,
                sectionedItemCallback
            )
        }
    }
}