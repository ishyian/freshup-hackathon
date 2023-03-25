package com.melitopolcherry.hackathon.adapters.tracking_section

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.databinding.ViewTrackingHeaderBinding

class TrackingAdapter : RecyclerView.Adapter<TrackingAdapter.HeaderViewHolder?>() {
    private var onItemClickListener: ItemTrackingCallback.Callback? = null
    var sourceList: List<TrackedSection>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            ViewTrackingHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = HeaderViewHolder(binding)
        binding.showAllButton.setOnClickListener {
            sourceList?.get(holder.bindingAdapterPosition)?.type?.let {
                onItemClickListener?.showAllEvents(it)
            }
        }
        return holder
    }

    fun setItemClickListener(clickListener: ItemTrackingCallback.Callback) {
        onItemClickListener = clickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<TrackedSection>) {
        sourceList = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(itemHolder: HeaderViewHolder, position: Int) =
        itemHolder.bind(sourceList?.get(itemHolder.bindingAdapterPosition)!!)

    override fun getItemCount(): Int {
        return if (sourceList?.size != null) {
            sourceList?.size!!
        } else {
            0
        }
    }

    inner class HeaderViewHolder(val binding: ViewTrackingHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemList: TrackedSection) {

            if (itemList.showSeparator) {
                binding.headerSeparator.visibility = View.GONE
            }

            binding.headerTitle.text = itemList.header

            binding.rvHeadline.adapter =
                TrackingItemsAdapter(itemList.trackedList, onItemClickListener)
        }
    }
}