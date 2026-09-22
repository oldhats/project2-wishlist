package com.example.project2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project2.databinding.ItemWishlistBinding

/**
 * RecyclerView Adapter for displaying items in the Wishlist.
 *
 * @param items List of [WishlistItem] objects to display.
 * @param onItemClick Callback triggered when an item is tapped (opens item URL).
 * @param onItemLongClick Callback triggered when an item is long-pressed (prompts item deletion).
 */
class WishlistAdapter(
    private val items: MutableList<WishlistItem>,
    private val onItemClick: (WishlistItem) -> Unit,
    private val onItemLongClick: (WishlistItem, Int) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemWishlistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWishlistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvItemName.text = item.name

            // Format price string neatly with '$' prefix if not present
            val formattedPrice = if (item.price.startsWith("$")) item.price else "$${item.price}"
            tvItemPrice.text = formattedPrice

            tvItemUrl.text = item.url

            // Set click listeners for item interactions
            root.setOnClickListener {
                onItemClick(item)
            }

            root.setOnLongClickListener {
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onItemLongClick(item, currentPosition)
                }
                true
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: WishlistItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun removeItem(position: Int): WishlistItem? {
        if (position in items.indices) {
            val removedItem = items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size - position)
            return removedItem
        }
        return null
    }

    fun insertItem(position: Int, item: WishlistItem) {
        if (position in 0..items.size) {
            items.add(position, item)
            notifyItemInserted(position)
        }
    }
}
