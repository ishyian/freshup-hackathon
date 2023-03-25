package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.databinding.ItemLandingPageBinding

class LandingPageVenueAdapter(val adapterOnClick: (EventsItem) -> Unit) :
    RecyclerView.Adapter<LandingPageVenueAdapter.ViewHolder>() {

    var listOfEvents = arrayListOf<EventsItem>()
    var place: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LandingPageVenueAdapter.ViewHolder {
        val binding =
            ItemLandingPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            adapterOnClick(listOfEvents[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<EventsItem>) {
        listOfEvents.clear()
        listOfEvents.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateData(newList: List<EventsItem>) {
        val position = listOfEvents.size
        listOfEvents.addAll(newList)
        notifyItemRangeInserted(position, newList.size)
        notifyItemChanged(position - 1)
    }

    override fun onBindViewHolder(holder: LandingPageVenueAdapter.ViewHolder, position: Int) =
        holder.bind(listOfEvents[holder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return listOfEvents.size
    }

    override fun onViewRecycled(holder: LandingPageVenueAdapter.ViewHolder) {
        if(holder.binding.root.context != null){
            holder.binding.eventImage.let {
//                Glide.with(it.context).clear(it)
            }
        }
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val binding: ItemLandingPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventsItem) {
            binding.itemTitle.text = event.title

            binding.itemDate.text = DateHelper.dateForHomeEvent(event.startDate)

            binding.eventImage.load(event.imageUrl){
                bitmapConfig(Bitmap.Config.RGB_565)
                placeholder(R.drawable.placeholder_event)
                error(R.drawable.placeholder_event)
            }
        }
    }
}