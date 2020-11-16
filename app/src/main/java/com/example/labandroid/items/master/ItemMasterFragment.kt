package com.example.labandroid.items.master

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.labandroid.R
import com.example.labandroid.items.api.ItemApi
import com.example.labandroid.items.api.NotificationHandler
import com.example.labandroid.items.data.ItemEvent
import com.example.labandroid.utils.API
import com.example.labandroid.utils.TAG
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_item_master.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ItemMasterFragment : Fragment() {

    private lateinit var itemViewModel: ItemMasterViewModel
    private lateinit var itemListAdapter: ItemRecyclerViewAdapter

    private var isActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_master, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        isActive = true

        itemListAdapter = ItemRecyclerViewAdapter(this)
        recycler_view.adapter = itemListAdapter

        itemViewModel = ViewModelProvider(this).get(ItemMasterViewModel::class.java)

        itemViewModel.items.observe(viewLifecycleOwner, {
            Log.v(TAG, "update items")
            if (it != null)
                itemListAdapter.itemList = it
        })

        itemViewModel.loading.observe(viewLifecycleOwner, { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        })

        itemViewModel.loadingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                Log.d(TAG, exception.message.toString())
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        })
        itemViewModel.loadItems()

        fab.setOnClickListener {
            Log.v(TAG, "add new item")
            findNavController().navigate(R.id.ItemDetailFragment)
        }

        CoroutineScope(Dispatchers.Main).launch { collectEvents() }

    }

    // collect Server notifications
    private suspend fun collectEvents() {
        while (isActive) {
            val event = NotificationHandler.eventChannel.receive()
            Log.d(TAG, "received $event")
            val itemEvent = API.gson.fromJson(event, ItemEvent::class.java)
            if (itemEvent.event == "created") {
                itemViewModel.addItem(itemEvent.payload.item)
            }
        }
    }


    override fun onStop() {
        super.onStop()
        isActive = false
    }

}