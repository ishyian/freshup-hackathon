package com.melitopolcherry.hackathon.adapters.search_section

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
import com.melitopolcherry.hackathon.data.models.comprehensive.TopItem
import com.melitopolcherry.hackathon.data.models.comprehensive.VenuesItem
import com.melitopolcherry.hackathon.databinding.ItemSearchBinding

class SearchItemsAdapter(
    private val searchList: List<Any>?,
    var searchText: String?,
    private val onItemClickListener: ItemSearchCallback.Callback?
) : RecyclerView.Adapter<SearchItemsAdapter.SearchItemViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val binding =
            ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = SearchItemViewHolder(binding)
        binding.root.setOnClickListener {
            searchList?.get(holder.bindingAdapterPosition)?.let {
                onItemClickListener?.onClick(it)
            }
        }
        return holder
    }

    override fun onBindViewHolder(itemHolder: SearchItemViewHolder, position: Int) =
        itemHolder.bind(searchList?.get(itemHolder.bindingAdapterPosition)!!)

    private fun setSpannableTitle(title: String?, itemHolder: ItemSearchBinding) {
        if(title != null && title.isNotEmpty()){
            val start = title.lowercase()
                .indexOf(searchText?.lowercase()!!)
            val end = start + searchText?.length!!
            val spannableString = SpannableString(title)
            if (start != -1 && end != -1) {
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD), start, end,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            itemHolder.root.context,
                            R.color.text_secondary
                        )
                    ),
                    start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
            }

            itemHolder.searchItemTitle.text = spannableString
        }else{
            itemHolder.searchItemTitle.text = title
        }
    }

    override fun onViewRecycled(holder: SearchItemsAdapter.SearchItemViewHolder) {
        if(holder.binding.root.context !=null){
            holder.binding.searchImageCircle.let {
//                Glide.with(it.context).clear(it)
            }
        }
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return searchList?.size ?: 0
    }

    inner class SearchItemViewHolder(var binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any) {
            when (item) {
                is EventsItem -> {
                    setSpannableTitle(item.title, binding)

                    binding.searchItemDescription.text =
                        item.startDate?.let { DateHelper.dateForEvent(it) }
                    if (item.venue != null) {
                        binding.searchItemPlace.text = item.venue?.getShortPlace()
                        binding.searchItemPlace.visibility = View.VISIBLE
                    }
                    binding.searchImageRounded.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_event)
                        error(R.drawable.placeholder_event)
                    }
                }
                is TopItem -> {
                    setSpannableTitle(item.title, binding)
                    binding.searchItemDescription.text = DateHelper.dateForEvent(item.startDate!!)
                    if (item.venue != null) {
                        binding.searchItemPlace.text = item.venue?.getShortPlace()
                        binding.searchItemPlace.visibility = View.VISIBLE
                    }
                    binding.searchImageCircle.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_event)
                        error(R.drawable.placeholder_event)
                    }
                }
                is VenuesItem -> {
                    setSpannableTitle(item.title, binding)
                    binding.searchItemDescription.text = item.place
                    binding.searchImageCircle.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_venue)
                        error(R.drawable.placeholder_venue)
                    }
                }
                is PerformersItem -> {
                    setSpannableTitle(item.title, binding)
                    binding.searchItemDescription.text = item.type
                    if (item.venue != null) {
                        binding.searchItemPlace.text = item.venue?.getShortPlace()
                        binding.searchItemPlace.visibility = View.VISIBLE
                    }
                    binding.searchImageCircle.load(item.imageUrl){
                        bitmapConfig(Bitmap.Config.RGB_565)
                        placeholder(R.drawable.placeholder_performer)
                        error(R.drawable.placeholder_performer)
                    }
                }
            }
        }
    }
}