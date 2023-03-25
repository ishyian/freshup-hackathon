package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.adapters.callbacks.TicketsCallback
import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.databinding.ItemBottomSheetBinding
import kotlin.math.ceil

class DetailsBottomSheetAdapter(private  val ticketsClickCallback: TicketsCallback.Callback) : RecyclerView.Adapter<DetailsBottomSheetAdapter.ViewHolder>() {

    private var list: ArrayList<TicketGroup> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            list[holder.bindingAdapterPosition].let { tg ->
                ticketsClickCallback.onClick(tg, binding)
            }
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: ArrayList<TicketGroup>) {
        list = newList
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TicketGroup) {
            binding.ticketSection.text = "Section: ${item.section}, Row: ${item.row}"
            binding.ticketRow.text =
                "Quantity: ${item.availableQuantity}, Format: ${item.format}"
            item.retailPrice?.let {
                binding.priceTicket.text = "\$${ceil(it).toInt()}"
            }
        }
    }
}