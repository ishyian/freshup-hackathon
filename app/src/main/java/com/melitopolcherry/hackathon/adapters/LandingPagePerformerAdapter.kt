package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.databinding.ItemLandingPagePerformerBinding

class LandingPagePerformerAdapter(val adapterOnClick: (EventsItem) -> Unit) :
    RecyclerView.Adapter<LandingPagePerformerAdapter.ViewHolder>() {

    var listOfEvents = arrayListOf<EventsItem>()
    private var listOfSep = arrayListOf<Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLandingPagePerformerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            adapterOnClick(listOfEvents[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(
        newList: List<EventsItem>,
    ) {
        listOfSep.clear()
        listOfEvents.clear()
        listOfEvents.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateData(
        newList: List<EventsItem>,
    ) {
        listOfSep.clear()
        val position = listOfEvents.size
        listOfEvents.addAll(newList)
        notifyItemRangeInserted(position, newList.size)
        notifyItemChanged(position - 1)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listOfEvents[holder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return listOfEvents.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        if (holder.binding.root.context != null) {
            holder.binding.eventImage.let {
                //                Glide.with(it.context).clear(it)
            }
        }
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val binding: ItemLandingPagePerformerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventsItem) = with(binding) {
            eventImage.load(event.imageUrl) {
                bitmapConfig(Bitmap.Config.RGB_565)
                placeholder(R.drawable.placeholder_event)
                error(R.drawable.placeholder_event)
            }

            itemTitle.text = event.title
            itemDate.text = DateHelper.dateForHomeEvent(event.startDate)

            itemAddress.isVisible = event.venue != null
            itemAddress.text = event.venue?.getPerformerVenuePlace() ?: ""
        }
    }
}