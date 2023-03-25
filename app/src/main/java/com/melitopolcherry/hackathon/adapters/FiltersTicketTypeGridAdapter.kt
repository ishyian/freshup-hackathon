package com.melitopolcherry.hackathon.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.ItemGridFiltersBinding

class FiltersTicketTypeGridAdapter(
    private var selectedTypes: ArrayList<Enums.TicketFormats>,
    private val adapterOnClick: (Enums.TicketFormats) -> Unit
) : RecyclerView.Adapter<FiltersTicketTypeGridAdapter.ViewHolder>() {

    private val types: List<Enums.TicketFormats> = listOf(
        Enums.TicketFormats.PHYSICAL,
        Enums.TicketFormats.ETICKET,
        Enums.TicketFormats.FLASH_SEATS,
        Enums.TicketFormats.TM_MOBILE,
        Enums.TicketFormats.TM_MOBILE_LINK,
        Enums.TicketFormats.GUEST_LIST,
        Enums.TicketFormats.PAPERLESS,
        Enums.TicketFormats.DIRECT_TRANSFER,
        Enums.TicketFormats.WILL_CALL,
        Enums.TicketFormats.UPLOAD_QR_CODE
    )

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
        fun bind(item: Enums.TicketFormats) {

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

            binding.gridItemText.text = item.getFormattedName(item)
        }
    }
}