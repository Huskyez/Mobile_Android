package com.example.labandroid.items.master

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.labandroid.items.data.Item
import com.example.labandroid.items.data.ItemDatabase
import com.example.labandroid.items.data.ItemRepository
import com.example.labandroid.utils.TAG
import com.example.labandroid.utils.Result
import kotlinx.coroutines.launch

class ItemMasterViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableItems = MutableLiveData<List<Item>>().apply { value = emptyList() }
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val items: LiveData<List<Item>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    private val itemRepo: ItemRepository

    init {
        val itemDao = ItemDatabase.getDatabase(application.applicationContext).itemDao()
        itemRepo = ItemRepository(itemDao)
        items = itemRepo.getAll()
        Log.d(TAG, "items in cache:  ${items.value}")
    }

    fun loadItems() {
        viewModelScope.launch {
            Log.v(TAG, "loadItems...")

            mutableLoading.value = true
            mutableException.value = null

            when (val result = itemRepo.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "Refresh succeeded")
                }
                is Result.Error -> {
                    Log.e(TAG, "Refresh failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false

//            try {
//                mutableItems.value = itemRepo.cache.value
//                Log.d(TAG, "loadItems succeeded")
//                mutableLoading.value = false
//            } catch (e: Exception) {
//                Log.w(TAG, "loadItems failed", e)
//                mutableException.value = e
//                mutableLoading.value = false
//            }
        }
    }


    fun addItem(item: Item) {
        mutableItems.value = mutableItems.value?.plus(item)
        viewModelScope.launch {
            Log.d(TAG, "Saving received item: $item")
            itemRepo.addToCache(item)
        }
    }


}