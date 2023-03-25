package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.databinding.ItemQuantityTicketBinding

class CheckoutQuantityAdapter(private val quantitySelectedListener: QuantitySelectedListener) :
    RecyclerView.Adapter<CheckoutQuantityAdapter.ViewHolder>() {

    private var list: List<Int>? = null
    private var selectedQuant: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemQuantityTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<Int>, selectedQuant: Int?) {
        list = newList
        this.selectedQuant = selectedQuant
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(list?.get(position)!!)

    override fun getItemCount(): Int {
        return if (list == null) {
            0
        } else {
            list?.size!!
        }
    }

    inner class ViewHolder(val binding: ItemQuantityTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Int) {
            binding.quantityNum.text = item.toString()

            binding.quantityBack.setOnClickListener {
                selectedQuant = item
                quantitySelectedListener.onQuantitySelected(item)
            }
        }
    }

    interface QuantitySelectedListener {
        fun onQuantitySelected(count: Int)
    }
}