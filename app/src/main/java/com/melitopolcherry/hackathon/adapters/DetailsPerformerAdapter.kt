package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.databinding.ItemDetailsPerfomerBinding

class DetailsPerformerAdapter(val adapterOnClick: (PerformersItem) -> Unit) :
    RecyclerView.Adapter<DetailsPerformerAdapter.ViewHolder>() {

    private var list: List<PerformersItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDetailsPerfomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            adapterOnClick(list[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<PerformersItem>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemDetailsPerfomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PerformersItem) {
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                binding.namePerformer,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            )

            binding.namePerformer.text = item.name
            binding.descriptionPerformer.text = item.type

            binding.imagePerformer.load(item.imageUrl){
                bitmapConfig(Bitmap.Config.RGB_565)
                    .placeholder(
                        if (item.subType == 1) {
                            R.drawable.placeholder_venue
                        } else {
                            R.drawable.placeholder_performer
                        }
                    )
                    .error(
                        if (item.subType == 1) {
                            R.drawable.placeholder_venue
                        } else {
                            R.drawable.placeholder_performer
                        }
                    )
            }
        }
    }
}