package com.melitopolcherry.hackathon.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.ItemGridFiltersBinding

class FiltersSeatTypeGridAdapter(
    private val adapterOnClick: (Enums.TicketType) -> Unit
) : RecyclerView.Adapter<FiltersSeatTypeGridAdapter.ViewHolder>() {

    private var selectedTypes: ArrayList<Enums.TicketType> = arrayListOf()
    private val types: List<Enums.TicketType> = listOf(
        Enums.TicketType.EVENT,
        Enums.TicketType.PARKING,
        Enums.TicketType.ADA)

   fun setData(l: List<Enums.TicketType>){
       selectedTypes.clear()
       selectedTypes.addAll(l)
       notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemGridFiltersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            if (selectedTypes.contains(types[holder.bindingAdapterPosition])) {
                selectedTypes.remove(types[holder.bindingAdapterPosition])
            } else {
                selectedTypes.add(types[holder.bindingAdapterPosition])
            }
            adapterOnClick.invoke(types[holder.bindingAdapterPosition])
            notifyItemChanged(holder.bindingAdapterPosition)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(types[position])

    override fun getItemCount(): Int {
        return types.size
    }

    inner class ViewHolder(val binding: ItemGridFiltersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Enums.TicketType) {
            if (selectedTypes.contains(item)) {
                binding.gridItem.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.filters_background_selected
                )
                binding.gridItemText.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.text_accent
                    )
                )
            } else {
                binding.gridItem.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.quantity_background
                )
                binding.gridItemText.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.text_primary
                    )
                )
            }

            binding.gridItemText.text = item.getName(item)
        }
    }
}