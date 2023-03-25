package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.core.extensions.px
import com.melitopolcherry.hackathon.data.models.SmallCategory
import com.melitopolcherry.hackathon.databinding.ItemSmallCategoryBinding

class SmallCategoriesAdapter(
    private var list: List<SmallCategory>,
    private var clearAll: SmallCategory,
    private var add: SmallCategory
) :
    RecyclerView.Adapter<SmallCategoriesAdapter.ViewHolder>() {

    private var selectedCategories: ArrayList<SmallCategory> = ArrayList()
    private var smallCategorySelectedListener: OnSmallCategorySelectedListener? = null

    fun setOnSmallCategorySelectedListener(onSmallCategorySelectedListener: OnSmallCategorySelectedListener?) {
        smallCategorySelectedListener = onSmallCategorySelectedListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedCategories(list: ArrayList<SmallCategory>) {
        if (selectedCategories != list) {
            selectedCategories = list
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSmallCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)

        binding.root.setOnClickListener {
            val eventCategory = list[holder.bindingAdapterPosition]
            if (eventCategory == clearAll) {
                selectedCategories.clear()
                smallCategorySelectedListener?.allButtonClicked()
                binding.root.isSelected = true
            } else if (eventCategory == add) {
                smallCategorySelectedListener?.addButtonClicked()
            } else if (selectedCategories.contains(eventCategory) && eventCategory != clearAll && eventCategory != add) {
                val index = selectedCategories.indexOf(eventCategory)
                selectedCategories.removeAt(index)
                if (selectedCategories.size == 0) {
                    selectedCategories.add(clearAll)
                }
            } else {
                selectedCategories.remove(clearAll)
                selectedCategories.add(eventCategory)
            }

            notifyItemChanged(holder.bindingAdapterPosition)
            smallCategorySelectedListener?.smallCategorySelected(selectedCategories)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemSmallCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SmallCategory) {
            if (selectedCategories.size == 0) {
                selectedCategories.add(clearAll)
            }
            val backgroundShape =
                ContextCompat.getDrawable(itemView.context, R.drawable.filters_button_background) as GradientDrawable

            if (selectedCategories.contains(item)) {
                backgroundShape.setStroke(2.px, Color.parseColor(item.color))
                backgroundShape.setColor(Color.parseColor("#FDF8EE"))
            } else {
                backgroundShape.setStroke(0, Color.parseColor(item.color))
                backgroundShape.setColor(Color.parseColor("#FFFFFF"))
            }

            binding.smallCategoryTitleTextView.text = item.shortName
            binding.smallCategoryCard.background = backgroundShape

            try {
                val imageName = "${item.imageName}"
                val drawable = ContextCompat.getDrawable(
                    binding.root.context,
                    binding.root.context.resources.getIdentifier(
                        imageName, "drawable",
                        binding.root.context?.packageName
                    )
                )
                binding.smallCategoryTitleTextView.setCompoundDrawablesWithIntrinsicBounds(
                    drawable, null, null, null
                )
            } catch (ignore: Exception) {
                ignore.printStackTrace()
            }
        }
    }

    interface OnSmallCategorySelectedListener {
        fun smallCategorySelected(selectedCategories: ArrayList<SmallCategory>)
        fun allButtonClicked()
        fun addButtonClicked()
    }
}