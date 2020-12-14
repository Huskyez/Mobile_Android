package com.example.labandroid.items.master

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.labandroid.R
import com.example.labandroid.items.data.Item
import com.example.labandroid.items.detail.ItemDetailFragment
import com.example.labandroid.utils.TAG
import kotlinx.android.synthetic.main.layout_item_view.view.*

val ITEM_COMP = object : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
        // User ID serves as unique ID
        oldItem._id == newItem._id && oldItem.room_id == newItem.room_id

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
        // Compare full contents (note: Java users should call .equals())
        oldItem == newItem
}

class ItemPagingDataAdapter(private val fragment: Fragment) : PagingDataAdapter<Item, ItemPagingDataAdapter.ViewHolder>(ITEM_COMP) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemPagingDataAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_view, parent, false)

        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item: Item? = getItem(position) //if (itemList.size < position) null else itemList[position]

        holder.itemView.tag = item

        if (item == null) {
            holder.bindPlaceHolder()
        } else {
            holder.bind(item)
        }

//        holder.tvText.text = item.text
        holder.itemView.setOnClickListener { view ->
            Log.d(TAG, view.tag.toString())
//            val testItem = view.tag as Item
//            Log.d(TAG, testItem.toString())

            if (item != null) {
                fragment.findNavController().navigate(R.id.ItemDetailFragment, Bundle().apply {
                    putString(ItemDetailFragment.ITEM_ID, item._id)
                    item.room_id?.let { putInt(ItemDetailFragment.ROOM_ID, it) }
                })
//            } else {
//                Toast.makeText(this, "Item is loading", Toast.LENGTH_SHORT)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvText: TextView = view.item_text
        fun bindPlaceHolder() {
            tvText.text = "Loading..."
        }

        fun bind(item: Item) {
            tvText.text = item.text
        }
    }
}
