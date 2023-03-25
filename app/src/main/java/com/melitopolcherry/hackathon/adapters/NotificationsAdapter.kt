package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.models.notification.NotificationsItem
import com.melitopolcherry.hackathon.databinding.ItemNotificationBinding

class NotificationsAdapter : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    var listOfNotifications = ArrayList<NotificationsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {

        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<NotificationsItem>) {
        listOfNotifications.clear()
        listOfNotifications.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listOfNotifications[position])

    override fun getItemCount(): Int {
        return listOfNotifications.size
    }

    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationsItem) {

            binding.titleTextView.text = notification.text

            notification.createdAt?.let {
                binding.dateTextView.text = DateHelper.dateForNotifications(it)
            }
        }
    }
}