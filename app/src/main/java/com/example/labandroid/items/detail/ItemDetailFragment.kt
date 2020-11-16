package com.example.labandroid.items.detail

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
import com.example.labandroid.items.data.Item
import com.example.labandroid.utils.TAG
import kotlinx.android.synthetic.main.fragment_item_detail.*
import java.time.LocalDateTime


class ItemDetailFragment : Fragment() {

    companion object {
        const val ITEM_ID = "ITEM_ID"
    }


    private lateinit var itemViewModel: ItemDetailViewModel
    private var itemId: String? = null
    private var item: Item? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                itemId = it.get(ITEM_ID) as String?
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        itemViewModel = ViewModelProvider(this).get(ItemDetailViewModel::class.java)
//        itemViewModel.item.observe(viewLifecycleOwner, {
//            Log.v(TAG, "update items - $it")
//
//
//            tv_id.text = it.id
//            et_text.setText(it.text)
////            date_picker.updateDate(it.date.year, it.date.month.value, it.date.dayOfMonth)
//            tv_date.text = it.date.toString()
//            tv_version.text = it.version.toString()
//
//            if (itemId != null) {
//                button.text = "UPDATE"
//            } else {
//                button.text = "SAVE"
//            }
//        })
        itemViewModel.fetching.observe(viewLifecycleOwner, { fetching ->
            Log.v(TAG, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        })

        itemViewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                Log.e(TAG, message)
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        itemViewModel.completed.observe(viewLifecycleOwner, { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().popBackStack()
            }
        })

        if (itemId != null) {
            itemViewModel.loadItem(itemId as String).observe(viewLifecycleOwner, {
                Log.d(TAG, "load item: $itemId")
                if (it == null) {
                    return@observe
                }

                item = it

                updateInterface()
            })
        } else {
            item = Item("", "", LocalDateTime.now(), 0)
            updateInterface()
        }

        button.setOnClickListener {
            val id = tv_id.text.toString()
            val text = et_text.text.toString()

            val date = LocalDateTime.parse(tv_date.text)
            val version = Integer.parseInt(tv_version.text.toString())

            val newItem = Item(id, text, date, version)

            itemViewModel.saveOrUpdateItem(newItem)
        }
    }

    private fun updateInterface() {

        tv_id.text = item?._id
        et_text.setText(item?.text)
        tv_date.text = item?.date.toString()
        tv_version.text = item?.version.toString()

        if (item?._id.isNullOrEmpty()) {
            button.text = "SAVE"
        } else {
            button.text = "UPDATE"
        }

    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
////        if (itemId == null) {
////            tv_id.text = ""
////            et_text.text.clear()
////            et_date.text.clear()
////            et_version.text.clear()
////        } else {
////
////        }
//    }

}