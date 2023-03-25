package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.DateHelper.dateForHomeEvent
import com.melitopolcherry.hackathon.data.models.suggested.SuggestedItem
import com.melitopolcherry.hackathon.databinding.ItemSuggestedBinding

class SuggestedEventsAdapter(private val callback: OnSuggestedSelected) :
    RecyclerView.Adapter<SuggestedEventsAdapter.ViewHolder>() {

    var listOfSuggested = arrayListOf<SuggestedItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSuggestedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            listOfSuggested[holder.bindingAdapterPosition].let {
                callback.onSuggestedSelected(it)
            }
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<SuggestedItem>) {
        listOfSuggested.clear()
        listOfSuggested.addAll(list)
        notifyDataSetChanged()
    }

    fun updateData(list: List<SuggestedItem>) {
        val position = listOfSuggested.size
        listOfSuggested.addAll(list)
        notifyItemRangeInserted(position, list.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listOfSuggested[position])

    override fun getItemCount(): Int {
        return listOfSuggested.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        if (holder.binding.suggestedImage.context != null) {
            holder.binding.suggestedImage.let {
//                Glide.with(it.context).clear(it)
            }
        }
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val binding: ItemSuggestedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SuggestedItem) {
            binding.suggestedName.text = item.title
            binding.suggestedPlace.text = item.venue?.getShortPlace()

            binding.suggestedDate.text = dateForHomeEvent(item.startDate)

            binding.suggestedImage.load(item.imageUrl){
                bitmapConfig(Bitmap.Config.RGB_565)
                placeholder(R.drawable.placeholder_event)
                error(R.drawable.placeholder_event)
            }

        }
    }

    interface OnSuggestedSelected {
        fun onSuggestedSelected(suggestedItem: SuggestedItem)
    }
}