package com.melitopolcherry.hackathon.screens.select_faculty.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.core.glide.GlideApp
import com.melitopolcherry.hackathon.data.model.Faculty
import com.melitopolcherry.hackathon.databinding.ItemUniversityBinding

class FacultyListAdapter(private val itemSelectedListener: OnListItemSelectedListener) :
    RecyclerView.Adapter<FacultyListAdapter.ViewHolder>() {

    private var faculties = arrayListOf<Faculty>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemUniversityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            itemSelectedListener.itemSelected(faculties[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<Faculty>) {
        faculties.clear()
        faculties.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(newList: List<Faculty>) {
        val lastItemPos = faculties.size
        faculties.addAll(newList)
        notifyItemRangeInserted(lastItemPos, newList.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(faculties[holder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return faculties.size
    }

    override fun onViewRecycled(holder: FacultyListAdapter.ViewHolder) {
        holder.binding.eventListImage.let {
            //            Glide.with(it.context).clear(it)
        }
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val binding: ItemUniversityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Faculty) = with(binding) {
            textTitle.text = item.name
            textAddress.text = item.address

            GlideApp.with(itemView.context)
                .load(item.image)
                .into(eventListImage)

            textDescription.isVisible = false
        }
    }

    interface OnListItemSelectedListener {
        fun itemSelected(faculty: Faculty)
    }
}