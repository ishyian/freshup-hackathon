package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.models.comprehensive.EventsItem
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.data.models.comprehensive.VenuesItem
import com.melitopolcherry.hackathon.databinding.ItemTrackingBinding

class AllTrackingEntetiesAdapter(val adapterOnClick: (Any) -> Unit) :
    RecyclerView.Adapter<AllTrackingEntetiesAdapter.ViewHolder>() {

    private var list = arrayListOf<Any>()
    private var searchText: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            adapterOnClick(list[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<Any>, text: String?) {
        list.clear()
        list.addAll(newList)
        searchText = text
        notifyDataSetChanged()
    }

    fun updateData(newList: List<Any>, text: String?) {
        val position = list.size
        list.addAll(newList)
        searchText = text
        notifyItemRangeInserted(position, newList.size)
    }

    override fun onBindViewHolder(itemHolder: ViewHolder, position: Int) =
        itemHolder.bind(list[itemHolder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.binding.trackingImageRounded.let {
//            Co.with(it.context).clear(it)
        }
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val binding: ItemTrackingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any) {
            when (item) {
                is EventsItem -> {
                    if (searchText != null && searchText?.length!! >= 1) {
                        setSpannableString(item.title, binding)
                    } else {
                        binding.trackingTitle.text = item.title
                    }
                    binding.trackingDescription.text = item.title
                    item.startDate?.let {
                        binding.trackingDate.text =  item.startDate.let { DateHelper.dateForEvent(it) }
                        binding.trackingDate.visibility = View.VISIBLE
                    }

                    binding.trackingImageRounded.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_event)
                        error(R.drawable.placeholder_event)
                    }
                }
                is VenuesItem -> {
                    if (searchText != null && searchText?.length!! >= 1) {
                        setSpannableString(item.name, binding)
                    } else {
                        binding.trackingTitle.text = item.name
                    }
                    binding.trackingDescription.text = item.getShortPlace()

                    binding.trackingImageRounded.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_venue)
                        error(R.drawable.placeholder_venue)
                    }

                }
                is PerformersItem -> {
                    if (searchText != null && searchText?.length!! >= 1) {
                        setSpannableString(item.title, binding)
                    } else {
                        binding.trackingTitle.text = item.title
                    }
                    binding.trackingDescription.text = item.title

                    binding.trackingImageCircle.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_performer)
                        error(R.drawable.placeholder_performer)
                    }
                }
            }
        }

        private fun setSpannableString(title: String?, binding: ItemTrackingBinding) {
            val start = title?.lowercase()
                ?.indexOf(searchText?.lowercase()!!)
            val end = start!! + searchText?.length!!
            val spannableString = SpannableString(title)
            if (start != -1 && end != -1) {
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD), start, end,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.text_secondary
                        )
                    ), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
            binding.trackingTitle.text = spannableString
        }
    }
}