package com.example.sl.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sl.R
import com.example.sl.model.Shop

class ShopsAdapter(private val onClick: (Shop) -> Unit) :
    ListAdapter<Shop, ShopsAdapter.ShopsViewHolder>(ShopDiffCallback) {

    class ShopsViewHolder(itemView: View, val onClick: (Shop) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val shopTextView: TextView = itemView.findViewById(R.id.lineName)
        private var currentShop: Shop? = null

        init {
            itemView.setOnClickListener {
                currentShop?.let {
                    onClick(it)
                }
            }
        }

        fun bind(shop: Shop) {
            currentShop = shop
            shopTextView.text = shop.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_layout, parent, false)
        return ShopsViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ShopsViewHolder, position: Int) {
        val shop = getItem(position)
        holder.bind(shop)

    }

}

object ShopDiffCallback : DiffUtil.ItemCallback<Shop>() {
    override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean {
        return oldItem.id == newItem.id
    }
}
