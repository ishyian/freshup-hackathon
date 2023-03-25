package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.DateHelper.dateForHomeEvent
import com.melitopolcherry.hackathon.data.models.order.OrderItem
import com.melitopolcherry.hackathon.databinding.ItemTicketBinding

class OrdersAdapter(private val showTicketsButton:Boolean) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    var listOfOrders = arrayListOf<OrderItem>()
    private var ticketListItemSelectedListener: OnTicketListItemSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)

        binding.root.setOnClickListener {
            ticketListItemSelectedListener?.itemSelected(listOfOrders[holder.bindingAdapterPosition])
        }
        if(showTicketsButton){
            binding.showTicketsButton.setOnClickListener {
                ticketListItemSelectedListener?.showTicketsClicked(listOfOrders[holder.bindingAdapterPosition])
            }
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<OrderItem>) {
        listOfOrders.clear()
        listOfOrders.addAll(list)
        notifyDataSetChanged()
    }

    fun updateData(list: List<OrderItem>) {
        val position = listOfOrders.size
        listOfOrders.addAll(list)
        notifyItemRangeInserted(position, list.size)
    }

    fun setOnTicketListItemSelectedListener(eventListItemSelectedListener: OnTicketListItemSelectedListener) {
        ticketListItemSelectedListener = eventListItemSelectedListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listOfOrders[holder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return listOfOrders.size
    }

    override fun onViewRecycled(holder: OrdersAdapter.ViewHolder) {
        holder.binding.ticketImage.let {
//            Glide.with(it.context).clear(it)
        }
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val binding: ItemTicketBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ticket: OrderItem) {
            binding.ticketImage.load(ticket.image){
                bitmapConfig(Bitmap.Config.RGB_565)
                placeholder(R.drawable.placeholder_event)
                error(R.drawable.placeholder_event)
            }

            if(showTicketsButton){
                val quant = ticket.quantity?.toInt()
                binding.showTicketsButton.text = if (quant == 1) {
                    "Show $quant Ticket"
                } else {
                    "Show $quant Tickets"
                }
            }else{
                binding.showTicketsButton.visibility = View.GONE
            }

            binding.ticketPerformerName.text = ticket.name

            binding.ticketPlace.text = ticket.venue?.getFullPlace()
            binding.ticketStatus.text = ticket.state

            val date = if (ticket.time == null) {
                "2020-01-22T19:00:00Z"
            } else {
                ticket.time
            }

            binding.ticketDate.text = dateForHomeEvent(date)
         }
    }

    interface OnTicketListItemSelectedListener {
        fun itemSelected(order: OrderItem?)
        fun showTicketsClicked(order: OrderItem?)
    }
}