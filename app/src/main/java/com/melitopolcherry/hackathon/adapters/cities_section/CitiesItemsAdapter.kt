package com.melitopolcherry.hackathon.adapters.cities_section

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.models.City
import com.melitopolcherry.hackathon.databinding.ItemCityBinding

class CitiesItemsAdapter(
    private val trackingList: List<City>?,
    private val searchText: String?,
    private val onItemClickListener: SectionedItemCallback.Callback?
) : RecyclerView.Adapter<CitiesItemsAdapter.CitiesItemViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesItemViewHolder {
        val binding =
            ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = CitiesItemViewHolder(binding)
        binding.root.setOnClickListener {
            trackingList?.get(holder.bindingAdapterPosition)?.let { city ->
                onItemClickListener?.onClick(it, city)
            }
        }
        return holder
    }

    override fun onBindViewHolder(itemHolder: CitiesItemViewHolder, position: Int) =
        itemHolder.bind(trackingList?.get(itemHolder.bindingAdapterPosition)!!)

    override fun getItemCount(): Int {
        return trackingList?.size ?: 0
    }

    inner class CitiesItemViewHolder(var binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: City) {

            if (searchText != null && searchText.isNotEmpty()) {
                val cityName = "${item.name}, ${item.region}"
                val start = cityName.lowercase()
                    .indexOf(searchText.lowercase())
                val end = start + searchText.length
                val spannableString = SpannableString(cityName)
                if (start != -1 && end != -1) {
                    spannableString.setSpan(
                        StyleSpan(Typeface.BOLD), start, end,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    spannableString.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                        ), start,
                        end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }

                binding.cityNameTextView.text = spannableString
            } else {
                binding.cityNameTextView.text = item.name
            }
        }
    }
}