package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.adapters.callbacks.AutocompleteCallback
import com.melitopolcherry.hackathon.databinding.ItemAutocompleteBinding

class SearchAutocompleteAdapter(private val itemSearchCallback: AutocompleteCallback.Callback) : RecyclerView.Adapter<SearchAutocompleteAdapter.ViewHolder>() {

    var list = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAutocompleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            list[holder.bindingAdapterPosition].let {
                itemSearchCallback.onClick(it)
            }
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<String>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(list[position])

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemAutocompleteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.searchItemTitle.text = text
        }
    }
}