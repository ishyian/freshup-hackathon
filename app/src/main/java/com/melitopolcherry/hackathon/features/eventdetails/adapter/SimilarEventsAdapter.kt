package com.melitopolcherry.hackathon.features.eventdetails.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.DateHelper.dateForHomeEvent
import com.melitopolcherry.hackathon.data.models.SimilarEventItem
import com.melitopolcherry.hackathon.databinding.ItemSimilarEventBinding

class SimilarEventsAdapter(private val callback: OnSimilarEventSelected) :
    RecyclerView.Adapter<SimilarEventsAdapter.ViewHolder>() {

    private val similarEvents = arrayListOf<SimilarEventItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSimilarEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<SimilarEventItem>) {
        similarEvents.clear()
        similarEvents.addAll(list)
        notifyDataSetChanged()
    }

    fun updateData(list: List<SimilarEventItem>) {
        val position = similarEvents.size
        similarEvents.addAll(list)
        notifyItemRangeInserted(position, list.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(similarEvents[position])
        holder.itemView.setOnClickListener {
            similarEvents[holder.bindingAdapterPosition].let {
                callback.onSimilarEventSelected(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return similarEvents.size
    }

    inner class ViewHolder(private val binding: ItemSimilarEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SimilarEventItem) = with(binding) {
            suggestedName.text = item.title
            suggestedDate.text = dateForHomeEvent(item.startDate)
            suggestedImage.load(item.imageUrl) {
                bitmapConfig(Bitmap.Config.RGB_565)
                placeholder(R.drawable.placeholder_event)
                error(R.drawable.placeholder_event)
            }
        }
    }

    interface OnSimilarEventSelected {
        fun onSimilarEventSelected(similarEvent: SimilarEventItem)
    }
}