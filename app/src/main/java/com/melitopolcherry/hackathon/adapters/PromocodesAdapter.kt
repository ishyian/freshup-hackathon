package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.DateHelper.getExpirationLeftDays
import com.melitopolcherry.hackathon.data.models.responces.PromoCode
import com.melitopolcherry.hackathon.databinding.ItemPromocodeBinding

class PromocodesAdapter : RecyclerView.Adapter<PromocodesAdapter.ViewHolder>() {

    var listOfPromocodes = arrayListOf<PromoCode>()
    private var promocodeSelectedListener: OnPromocodeSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPromocodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            promocodeSelectedListener?.itemSelected(listOfPromocodes[holder.bindingAdapterPosition])
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<PromoCode>) {
        listOfPromocodes.clear()
        listOfPromocodes.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnTicketListItemSelectedListener(eventListItemSelectedListener: OnPromocodeSelectedListener) {
        promocodeSelectedListener = eventListItemSelectedListener
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listOfPromocodes[holder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return listOfPromocodes.size
    }

    inner class ViewHolder(val binding: ItemPromocodeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(promo: PromoCode) {
            binding.promocodeCode.text = promo.code?.lowercase()
            if (promo.percentage != null && promo.percentage!!) {
                binding.promocodeDiscount.text = "${promo.value?.toDouble()?.toInt()}%"
            } else {
                binding.promocodeDiscount.text = "$${promo.value?.toDouble()?.toInt()}"
            }

            if (promo.value?.toDouble()?.toInt()!! > 20) {
                binding.promocodeImage.setImageResource(R.drawable.promo_red)
            } else {
                binding.promocodeImage.setImageResource(R.drawable.promo_blue)
            }

            binding.promocodeDescription.text = promo.description
            binding.promocodeDate.text = getExpirationLeftDays(promo.validUntil!!)
        }
    }

    interface OnPromocodeSelectedListener {
        fun itemSelected(code: PromoCode?)
    }
}