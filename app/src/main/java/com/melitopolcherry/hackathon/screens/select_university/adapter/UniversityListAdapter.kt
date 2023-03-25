package com.melitopolcherry.hackathon.screens.select_university.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.core.glide.GlideApp
import com.melitopolcherry.hackathon.data.DateHelper.dateForHomeEvent
import com.melitopolcherry.hackathon.data.model.University
import com.melitopolcherry.hackathon.data.models.event.small.EventMap
import com.melitopolcherry.hackathon.databinding.ItemListEventBinding
import com.melitopolcherry.hackathon.databinding.ItemUniversityBinding

class UniversityListAdapter(private val itemSelectedListener: OnListItemSelectedListener) :
    RecyclerView.Adapter<UniversityListAdapter.ViewHolder>() {

    private var universities = arrayListOf<University>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemUniversityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            itemSelectedListener.itemSelected(universities[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<University>) {
        universities.clear()
        universities.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(newList: List<University>) {
        val lastItemPos = universities.size
        universities.addAll(newList)
        notifyItemRangeInserted(lastItemPos, newList.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(universities[holder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return universities.size
    }

    override fun onViewRecycled(holder: UniversityListAdapter.ViewHolder) {
        holder.binding.eventListImage.let {
            //            Glide.with(it.context).clear(it)
        }
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val binding: ItemUniversityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: University) = with(binding) {
            textTitle.text = item.name
            textDescription.text = item.address

            GlideApp.with(itemView.context)
                .load(item.image)
                .placeholder(R.drawable.placeholder_event)
                .error(R.drawable.placeholder_event)
                .into(eventListImage)

            textDescription.text = item.description
        }
    }

    interface OnListItemSelectedListener {
        fun itemSelected(university: University)
    }
}