package com.melitopolcherry.hackathon.adapters.search_section

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.databinding.ViewSearchHeaderBinding

class SearchAdapter(private val itemSearchCallback: ItemSearchCallback.Callback) :
    RecyclerView.Adapter<SearchAdapter.NewsViewHolder?>() {

    var sourceList: List<SearchSection>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding =
            ViewSearchHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<SearchSection>) {
        sourceList = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(itemHolder: NewsViewHolder, position: Int) =
        itemHolder.bind(sourceList?.get(itemHolder.bindingAdapterPosition)!!)

    override fun getItemCount(): Int {
        return if (sourceList?.size != null) {
            sourceList?.size!!
        } else {
            0
        }
    }

    inner class NewsViewHolder(var binding: ViewSearchHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchSection) {
            binding.headerTitle.text = item.header

            binding.rvHeadline.adapter = SearchItemsAdapter(
                item.searchList,
                item.searchText,
                itemSearchCallback
            )
        }
    }
}