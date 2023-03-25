package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.callbacks.ItemTicketGroupCallback
import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.data.utils.DrawableHelper
import com.melitopolcherry.hackathon.databinding.ItemTicketGroupBinding

class TicketGroupAdapter : RecyclerView.Adapter<TicketGroupAdapter.ViewHolder>() {
    private var viewHeight = 0
    var recyclerHeight = 0

    private var tickets: List<TicketGroup> = arrayListOf()
    var selectedTicket: TicketGroup? = null
    private var selectedTicketView: ItemTicketGroupBinding? = null
    private var defaultSectorColor: Int? = null
    private var selectTicketCallback: ItemTicketGroupCallback.Callback? = null
    private var sectorColorsMap: HashMap<String, Int>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTicketGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        binding.root.post {
            viewHeight = binding.root.height
        }
        binding.root.setOnClickListener {
            if (tickets.isNotEmpty()) {
                val positionInList = holder.bindingAdapterPosition % tickets.size
                val ticketAdapter = tickets[positionInList]
                if (selectedTicket == null) {
                    selectItem(binding)
                    selectedTicket = ticketAdapter
                    selectedTicketView = binding
                    selectTicketCallback?.onClick(ticketAdapter, binding)
                } else if (selectedTicket == ticketAdapter) {
                    selectedTicketView = binding
                    selectTicketCallback?.onClick(ticketAdapter, binding)
                } else if (selectedTicket != ticketAdapter && selectedTicket != null) {
                    selectedTicketView?.let { it2 -> unselectItem(it2) }
                    selectItem(binding)
                    selectedTicketView = binding
                    selectedTicket = ticketAdapter
                    selectTicketCallback?.onClick(ticketAdapter, binding)
                }
            }
        }

        defaultSectorColor = ContextCompat.getColor(binding.root.context, R.color.colorAccent)
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<TicketGroup>, colorsMap: HashMap<String, Int>) {
        tickets = newList
        sectorColorsMap = colorsMap
        notifyDataSetChanged()
    }

    fun highlight(item: ItemTicketGroupBinding) {
        if (selectedTicketView != item) {
            if (selectedTicketView != null) {
                unselectItem(selectedTicketView)
            }
            selectedTicketView = item
            selectItem(selectedTicketView)
        }
    }

    fun setCallback(callback: ItemTicketGroupCallback.Callback) {
        selectTicketCallback = callback
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (tickets.isNotEmpty()) {
            val positionInList = holder.bindingAdapterPosition % tickets.size
            holder.bind(tickets[positionInList])
        }
    }

    override fun getItemCount(): Int {
        return if (tickets.isNotEmpty()) {
            tickets.size
        } else {
            0
        }
    }

    private fun selectItem(itemView: ItemTicketGroupBinding?) {
        println("🍭sssss ")
        if (itemView != null) {
            println("🍭sssss NULL")
            val drawableHelper = DrawableHelper()
            val viewColor = itemView.ticketColor.background
            if (viewColor != null) {
                println("🍭sssss NULL 2")
                val primaryColor =
                    drawableHelper.getShadeOfColor((viewColor as ColorDrawable).color, 0.8f)

                val secondaryColor = drawableHelper.getShadeOfColor(viewColor.color, 0.7f)
                val secondaryPriceColor = drawableHelper.getShadeOfColor(primaryColor, 0.7f)

                val backgroundColor = drawableHelper.adjustAlpha(viewColor.color, 0.05f)

                itemView.ticketSection.setTextColor(primaryColor)
                itemView.priceTicket.setTextColor(primaryColor)
                itemView.ticketRow.setTextColor(secondaryColor)
                itemView.priceEaText.setTextColor(secondaryPriceColor)
                itemView.separatorView.setBackgroundColor(backgroundColor)
                itemView.root.setBackgroundColor(backgroundColor)
            }
        }
    }

    private fun unselectItem(itemView: ItemTicketGroupBinding?) {
        val textColor: Int
        val itemBackground: Int
        val dividerColor: Int
        val secondaryColor: Int

        if (itemView != null) {
            val context = itemView.root.context

            textColor = ContextCompat.getColor(context, R.color.text_primary)
            itemBackground =
                ContextCompat.getColor(context, R.color.background_primary)
            dividerColor = ContextCompat.getColor(context, R.color.separator)
            secondaryColor = ContextCompat.getColor(context, R.color.text_secondary)

            itemView.root.setBackgroundColor(itemBackground)
            itemView.separatorView.setBackgroundColor(dividerColor)
            itemView.ticketSection.setTextColor(textColor)
            itemView.priceTicket.setTextColor(textColor)
            itemView.ticketRow.setTextColor(secondaryColor)
            itemView.priceEaText.setTextColor(secondaryColor)
        }
        selectedTicketView = null
    }

    inner class ViewHolder(val binding: ItemTicketGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ticket: TicketGroup) {
            if (bindingAdapterPosition == tickets.lastIndex && tickets.size > 1) {
                val params = binding.root.layoutParams as RecyclerView.LayoutParams
                params.bottomMargin = recyclerHeight - viewHeight
                binding.root.layoutParams = params
                println("🍰 $recyclerHeight || $viewHeight")
            } else {
                val params = binding.root.layoutParams as RecyclerView.LayoutParams
                params.bottomMargin = 0
                binding.root.layoutParams = params
            }

            val sectorColor = sectorColorsMap?.get(ticket.section)
            if (sectorColor != null) {
                binding.ticketColor.setBackgroundColor(sectorColor)
            } else {
                defaultSectorColor?.let {
                    binding.ticketColor.setBackgroundColor(it)
                }
            }

            binding.ticketSection.text = binding.root.context.getString(
                R.string.section_row_template,
                ticket.section,
                ticket.row
            )

            binding.ticketRow.text = binding.root.context.getString(
                R.string.quantity_format_template,
                ticket.availableQuantity,
                ticket.format
            )

            ticket.retailPrice?.let {
                binding.priceTicket.text = binding.root.context.getString(
                    R.string.price_int_template, kotlin.math.ceil(
                        it
                    ).toInt()
                )
            }
        }
    }
}