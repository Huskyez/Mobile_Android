package com.example.labandroid.items.master

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import com.example.labandroid.R
import com.example.labandroid.items.api.NotificationHandler
import com.example.labandroid.items.data.ItemEvent
import com.example.labandroid.utils.API
import com.example.labandroid.utils.TAG
import kotlinx.android.synthetic.main.fragment_item_master.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class ItemMasterFragment : Fragment() {

    private lateinit var itemViewModel: ItemMasterViewModel
    private lateinit var itemListAdapter: ItemPagingDataAdapter

    private var isActive: Boolean = false

    private var queryString: String = ""

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isActive = true

        itemListAdapter = ItemPagingDataAdapter(this)
        recycler_view.adapter = itemListAdapter

        itemViewModel = ViewModelProvider(this).get(ItemMasterViewModel::class.java)


        lifecycleScope.launch {
            itemViewModel.itemFlow
                .map{ pagingData -> pagingData.filter { item -> item.text.contains(queryString) } }
                .collectLatest { pagingData -> itemListAdapter.submitData(pagingData) }
        }

//        itemViewModel.itemsPager.observe(viewLifecycleOwner, {
//            Log.v(TAG, "update items")
//            if (it != null) {
//                lifecycleScope.launch {
//                    itemListAdapter.submitData(it)
//                }
//            }
//        })
        
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
//                    itemViewModel.itemFlow.map{}.collectLatest {  }
                    queryString = query
                    itemListAdapter.refresh()
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
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
//        itemViewModel.loadItems()

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