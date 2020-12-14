package com.example.labandroid.items.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.labandroid.items.data.Item
import com.example.labandroid.items.local.ItemDatabase
import com.example.labandroid.items.data.ItemRepository
import com.example.labandroid.utils.Result
import com.example.labandroid.utils.TAG
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ItemDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableItem = MutableLiveData<Item>().apply { value = Item("", 0,"", LocalDateTime.now(), 0) }
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val item: LiveData<Item> = mutableItem
    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    private val itemRepo : ItemRepository

    init {
        val itemDao = ItemDatabase.getDatabase(application.applicationContext).itemDao()
        itemRepo = ItemRepository(itemDao)
    }

    fun loadItem(room_id: Int) : LiveData<Item> {
        return itemRepo.find(room_id)
//        viewModelScope.launch {
//            Log.i(TAG, "loadItem...")
//            mutableFetching.value = true
//            mutableException.value = null
//
//            try {
//                mutableItem.value = itemRepo.find(itemId).value
//                Log.i(TAG, "loadItem succeeded - ${mutableItem.value}")
//                mutableFetching.value = false
//            } catch (e: Exception) {
//                Log.w(TAG, "loadItem failed", e)
//                mutableException.value = e
//                mutableFetching.value = false
//            }
//        }
    }

    fun saveOrUpdateItem(newItem: Item) {

        viewModelScope.launch {

            Log.i(TAG, "saveOrUpdateItem...")

            mutableFetching.value = true
            mutableException.value = null

            val result : Result<Item> = if (newItem._id.isNotEmpty()) {
                itemRepo.update(newItem._id, Item(newItem._id, newItem.room_id, newItem.text, LocalDateTime.now(), newItem.version + 1))
            } else {
                itemRepo.save(Item("", newItem.room_id, newItem.text, LocalDateTime.now(), 1))
            }

            when (result) {
                is Result.Success -> Log.d(TAG, "saveOrUpdate succeeded")
                is Result.Error -> {
                    Log.e(TAG, "saveOrUpdate failed", result.exception)
                    mutableException.value = result.exception
                }
                else -> Log.d(TAG, "loading")
            }

            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }


}