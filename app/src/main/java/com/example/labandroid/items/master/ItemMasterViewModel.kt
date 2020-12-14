package com.example.labandroid.items.master

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.labandroid.items.data.ItemRemoteMediator
import com.example.labandroid.items.api.ItemApi
import com.example.labandroid.items.data.Item
import com.example.labandroid.items.local.ItemDatabase
import com.example.labandroid.items.data.ItemRepository
import com.example.labandroid.utils.PaginationData
import com.example.labandroid.utils.TAG
import com.example.labandroid.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ItemMasterViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableItems = MutableLiveData<List<Item>>().apply { value = emptyList() }
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

//    val items: LiveData<List<Item>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    var itemFlow: Flow<PagingData<Item>>

    private val itemRepo: ItemRepository


    init {
        val itemDao = ItemDatabase.getDatabase(application.applicationContext).itemDao()
        itemRepo = ItemRepository(itemDao)

        val pager = Pager(
            config = PagingConfig(pageSize = PaginationData.pageSize),
            remoteMediator = ItemRemoteMediator(ItemDatabase.getDatabase(application.applicationContext), ItemApi.itemService),
            pagingSourceFactory = { itemDao.pagingSource() }
        )

        itemFlow = pager.flow
    }

//    fun loadItems() {
//        viewModelScope.launch {
//            Log.v(TAG, "loadItems...")
//
//            mutableLoading.value = true
//            mutableException.value = null
//
//            when (val result = itemRepo.refresh()) {
//                is Result.Success -> {
//                    Log.d(TAG, "Refresh succeeded")
//                }
//                is Result.Error -> {
//                    Log.e(TAG, "Refresh failed", result.exception)
//                    mutableException.value = result.exception
//                }
//            }
//            mutableLoading.value = false
//        }
//    }


    fun addItem(item: Item) {
        mutableItems.value = mutableItems.value?.plus(item)
        viewModelScope.launch {
            Log.d(TAG, "Saving received item: $item")
            itemRepo.addToCache(item)
        }
    }


}