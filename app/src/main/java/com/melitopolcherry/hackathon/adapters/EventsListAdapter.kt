package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.core.glide.GlideApp
import com.melitopolcherry.hackathon.data.DateHelper.dateForHomeEvent
import com.melitopolcherry.hackathon.data.models.event.small.EventMap
import com.melitopolcherry.hackathon.databinding.ItemListEventBinding

class EventsListAdapter(private val eventListItemSelectedListener: OnEventListItemSelectedListener) :
    RecyclerView.Adapter<EventsListAdapter.ViewHolder>() {

    private var listOfEvents = arrayListOf<EventMap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            eventListItemSelectedListener.itemSelected(listOfEvents[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<EventMap>) {
        listOfEvents.clear()
        listOfEvents.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(newList: List<EventMap>) {
        val lastItemPos = listOfEvents.size
        listOfEvents.addAll(newList)
        notifyItemRangeInserted(lastItemPos, newList.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listOfEvents[holder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return listOfEvents.size
    }

    override fun onViewRecycled(holder: EventsListAdapter.ViewHolder) {
        holder.binding.eventListImage.let {
            //            Glide.with(it.context).clear(it)
        }
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val binding: ItemListEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventMap) = with(binding) {
            eventTitle.text = event.title
            eventItemAddressTextView.text = event.venue?.getShortPlace()

            GlideApp.with(itemView.context)
                .load(event.imageUrl)
                .placeholder(R.drawable.placeholder_event)
                .error(R.drawable.placeholder_event)
                .into(eventListImage)

            dateTextView.text = dateForHomeEvent(event.startDate)
        }
    }

    interface OnEventListItemSelectedListener {
        fun itemSelected(event: EventMap)
    }
}