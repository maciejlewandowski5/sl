package com.example.sl.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sl.R
import com.example.sl.model.ItemElement

class ItemAdapter(private val onClick: (ItemElement) -> Unit) :
    ListAdapter<ItemElement, ItemAdapter.ItemViewHolder>(ItemDiffCallback) {

    class ItemViewHolder(itemView: View, val onClick: (ItemElement) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val itemTextView: TextView = itemView.findViewById(R.id.lineName)
        private var currentItem: ItemElement? = null

        init {
            itemView.setOnClickListener {
                currentItem?.let {
                    onClick(it)
                }
            }
        }

        fun bind(item: ItemElement) {
            currentItem = item
            itemTextView.text = item.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_layout, parent, false)
        return ItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

    }

}

object ItemDiffCallback : DiffUtil.ItemCallback<ItemElement>() {
    override fun areItemsTheSame(oldItem: ItemElement, newItem: ItemElement): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ItemElement, newItem: ItemElement): Boolean {
        return oldItem.id == newItem.id
    }
}