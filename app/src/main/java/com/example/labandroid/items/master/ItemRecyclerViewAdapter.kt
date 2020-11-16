package com.example.labandroid.items.master

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.labandroid.R
import com.example.labandroid.items.detail.ItemDetailFragment
import com.example.labandroid.items.data.Item
import com.example.labandroid.utils.TAG
import kotlinx.android.synthetic.main.layout_item_view.view.*

class ItemRecyclerViewAdapter(private val fragment: Fragment) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    var itemList = emptyList<Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_view, parent, false)

        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Log.v(TAG, "onBindViewHolder $position")
        val item = itemList[position]
        holder.itemView.tag = item
        holder.tvText.text = item.text
//        holder.tvDate.text = item.date.toString()
//        holder.tvVersion.text = item.version.toString()

        holder.itemView.setOnClickListener { view ->
            Log.d(TAG, view.tag.toString())
            val testItem = view.tag as Item
            Log.d(TAG, testItem.toString())
            fragment.findNavController().navigate(R.id.ItemDetailFragment, Bundle().apply {
                putString(ItemDetailFragment.ITEM_ID, item._id)
            })
        }
    }

    override fun getItemCount() = itemList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvText: TextView = view.item_text

    }
}