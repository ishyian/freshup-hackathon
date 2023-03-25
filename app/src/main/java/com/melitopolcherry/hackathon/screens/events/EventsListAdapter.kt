package com.melitopolcherry.hackathon.screens.events

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.data.model.EventItem
import com.melitopolcherry.hackathon.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EventsListAdapter(private val itemSelectedListener: OnListItemSelectedListener) :
    RecyclerView.Adapter<EventsListAdapter.ViewHolder>() {

    private var events = arrayListOf<EventItem>()

    private val sdf1 = SimpleDateFormat("dd-MM-yyyy hh:MM", Locale.getDefault())
    private val sdfStart = SimpleDateFormat("dd.MM.yyyy hh:MM", Locale.getDefault())
    private val sdfTo = SimpleDateFormat("hh:MM", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            itemSelectedListener.itemSelected(events[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<EventItem>) {
        events.clear()
        events.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(newList: List<EventItem>) {
        val lastItemPos = events.size
        events.addAll(newList)
        notifyItemRangeInserted(lastItemPos, newList.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(events[holder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return events.size
    }

    inner class ViewHolder(val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventItem) = with(binding) {
            textName.text = item.subjectName
            textAddress.text = "Аудиторія: ${item.classroom}"
            textTeacherName.text = item.teacherName
            val date = sdf1.parse(item.startDate)
            val dateEnd = sdf1.parse(item.endDate)
            textDate.text = "${sdfStart.format(date)} - ${sdfTo.format(dateEnd)}"
        }
    }

    interface OnListItemSelectedListener {
        fun itemSelected(event: EventItem)
    }
}