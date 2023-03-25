package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.DateHelper.formatWeatherDate
import com.melitopolcherry.hackathon.data.models.weather.hourly.ListItem
import com.melitopolcherry.hackathon.databinding.ItemWeatherBinding

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    private var weatherList = listOf<ListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.post {
            binding.root.minimumWidth = parent.width / 4
        }
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<ListItem>) {
        weatherList = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(weatherList[position])

    override fun getItemCount(): Int {
        return if (weatherList.isEmpty()) {
            0
        } else {
            4
        }
    }

    inner class ViewHolder(val binding: ItemWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(weatherItem: ListItem) {
            weatherItem.dtTxt?.let {
                binding.weatherTemp.text = binding.root.context.getString(
                    R.string.temp_template,
                    weatherItem.main?.temp?.toInt()
                )
                binding.weatherTime.text = formatWeatherDate(it)
            }

            val selectedImageName = "ic_${weatherItem.weather?.get(0)?.icon}"
            val resourceId = binding.root.resources.getIdentifier(
                selectedImageName, "drawable",
                binding.root.context.packageName
            )
            if (resourceId != 0) {
                binding.weatherIcon.setImageResource(resourceId)
            }
        }
    }
}