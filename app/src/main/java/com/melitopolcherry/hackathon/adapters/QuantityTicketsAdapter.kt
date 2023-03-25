package com.melitopolcherry.hackathon.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.callbacks.QuantityCallback
import com.melitopolcherry.hackathon.databinding.ItemQuantityTicketBinding

class QuantityTicketsAdapter(
    private val selectedList: ArrayList<Int>,
    private val callback: QuantityCallback.Callback
) :
    RecyclerView.Adapter<QuantityTicketsAdapter.ViewHolder>() {

    private var list = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemQuantityTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            if (selectedList.contains(list[holder.bindingAdapterPosition])) {
                selectedList.remove(list[holder.bindingAdapterPosition])
            } else {
                selectedList.add(list[holder.bindingAdapterPosition])
            }
            callback.onClick(selectedList)
            notifyItemChanged(holder.bindingAdapterPosition)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemQuantityTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Int) {

            if (selectedList.contains(item)) {
                binding.quantityBack.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.quantity_background_selected
                )
                binding.quantityNum.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.text_primary
                    )
                )
            } else {
                binding.quantityBack.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.quantity_background
                )
                binding.quantityNum.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.text_primary
                    )
                )
            }

            binding.quantityNum.text = item.toString()
        }
    }
}