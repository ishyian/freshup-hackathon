package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.models.event.ocurrences.OccurrencesItem
import com.melitopolcherry.hackathon.databinding.ItemDetailsDatesBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsDatesAdapter(val adapterOnClick: (OccurrencesItem) -> Unit) :
    RecyclerView.Adapter<DetailsDatesAdapter.ViewHolder>() {

    private var viewHeight = 0
    var recyclerHeight = 0
    private var listOfOccurrences = arrayListOf<OccurrencesItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDetailsDatesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            adapterOnClick(listOfOccurrences[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<OccurrencesItem>) {
        listOfOccurrences.clear()
        listOfOccurrences.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateData(newList: List<OccurrencesItem>) {
        val position = listOfOccurrences.size
        listOfOccurrences.addAll(newList)
        notifyItemRangeInserted(position, newList.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listOfOccurrences[position])

    override fun getItemCount(): Int {
        return listOfOccurrences.size
    }

    inner class ViewHolder(val binding: ItemDetailsDatesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: OccurrencesItem) {
            if (bindingAdapterPosition == listOfOccurrences.lastIndex) {
                val params = binding.root.layoutParams as RecyclerView.LayoutParams
                binding.root.post {
                    viewHeight = binding.root.height
                    params.bottomMargin = recyclerHeight - viewHeight + 8
                    binding.root.layoutParams = params
                    println("🍰 $recyclerHeight || $viewHeight")
                }
            } else {
                val params = binding.root.layoutParams as RecyclerView.LayoutParams
                params.bottomMargin = 8
                binding.root.layoutParams = params
            }

            binding.eventName.text = event.name
            binding.eventDescription.text = "${event.city?.buildingName}, ${event.city?.name}, ${event.city?.region}"

            val formattedDate = DateHelper.backendDateFormatter.parse(event.startDate!!)
            val dayString = SimpleDateFormat("MMM d", Locale.US).format(formattedDate!!)
            binding.eventTime.text = dayString

            val monthString = SimpleDateFormat("EEE hh:mm aa", Locale.US).format(formattedDate)
            binding.eventDayPart.text = monthString
        }
    }
}