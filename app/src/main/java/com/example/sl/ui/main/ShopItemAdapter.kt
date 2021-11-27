package com.example.sl.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sl.R


class FlowersAdapter(private val onClick: (Shop) -> Unit) :
    ListAdapter<Shop, FlowersAdapter.FlowerViewHolder>(FlowerDiffCallback) {


    class FlowerViewHolder(itemView: View, val onClick: (Shop) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val flowerTextView: TextView = itemView.findViewById(R.id.lineName)
        private var currentFlower: Shop? = null

        init {
            itemView.setOnClickListener {
                currentFlower?.let {
                    onClick(it)
                }
            }
        }

        fun bind(flower: Shop) {
            currentFlower = flower
            flowerTextView.text = flower.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_layout, parent, false)
        return FlowerViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
        val flower = getItem(position)
        holder.bind(flower)

    }

}

object FlowerDiffCallback : DiffUtil.ItemCallback<Shop>() {
    override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean {
        return oldItem.id == newItem.id
    }
}


class ItemAdpater(private val onClick: (ItemElement) -> Unit) :
    ListAdapter<ItemElement, ItemAdpater.ItemViewHolder>(ItemDiffCallback) {


    class ItemViewHolder(itemView: View, val onClick: (ItemElement) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val flowerTextView: TextView = itemView.findViewById(R.id.lineName)
        private var currentFlower: ItemElement? = null

        init {
            itemView.setOnClickListener {
                currentFlower?.let {
                    onClick(it)
                }
            }
        }

        fun bind(flower: ItemElement) {
            currentFlower = flower
            flowerTextView.text = flower.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_layout, parent, false)
        return ItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val flower = getItem(position)
        holder.bind(flower)

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