package com.melitopolcherry.hackathon.adapters.tracking_section

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.data.models.comprehensive.VenuesItem
import com.melitopolcherry.hackathon.databinding.ItemTrackingBinding

class TrackingItemsAdapter(
    private val trackingList: List<Any>?,
    private val onItemClickListener: ItemTrackingCallback.Callback?
) : RecyclerView.Adapter<TrackingItemsAdapter.TrackingViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val binding =
            ItemTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = TrackingViewHolder(binding)
        binding.root.setOnClickListener {
            trackingList?.get(holder.bindingAdapterPosition)?.let {
                onItemClickListener?.onItemClick(it)
            }
        }
        return holder
    }

    override fun onBindViewHolder(itemHolder: TrackingViewHolder, position: Int) =
        itemHolder.bind(trackingList?.get(itemHolder.bindingAdapterPosition)!!)

    override fun getItemCount(): Int {
        return trackingList?.size ?: 0
    }

    class TrackingViewHolder(var binding: ItemTrackingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any) {
            when (item) {
                is EventsItem -> {
                    binding.trackingTitle.text = item.title
                    binding.trackingDescription.text = item.title

                    item.startDate?.let {
                        binding.trackingDate.text =  item.startDate.let { DateHelper.dateForEvent(it) }
                        binding.trackingDate.visibility = View.VISIBLE
                    }
                    binding.trackingImageRounded.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_event)
                        error(R.drawable.placeholder_event)
                    }
                }
                is VenuesItem -> {
                    binding.trackingTitle.text = item.name
                    binding.trackingDescription.text = item.getShortPlace()
                    binding.trackingImageRounded.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_venue)
                        error(R.drawable.placeholder_venue)
                    }
                }
                is PerformersItem -> {
                    binding.trackingTitle.text = item.title
                    binding.trackingDescription.text = item.type
                    binding.trackingImageCircle.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_performer)
                        error(R.drawable.placeholder_performer)
                    }
                }
            }
        }
    }
}