package com.example.labandroid.items.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.labandroid.items.api.ItemApi
import com.example.labandroid.items.local.ItemDao
import com.example.labandroid.utils.*
import java.lang.Exception

class ItemRepository(private val itemDao : ItemDao) {

//    private var cache : MutableList<Item>? = null
//    val cache = MediatorLiveData<List<Item>>()
//    var itemsLiveData = itemDao.getAll()
//    val cache = itemDao.getAll()

    // items save here are meant to be send to the server when
    // connection is reestablished
//    private var cache: MutableList<ItemRequest> = mutableListOf()

//    private var offline: MutableLiveData<Boolean>? = null
//    val savedInCache: LiveData<Boolean>

//    suspend fun refresh() : Result<Boolean> {
//        return try {
//
//            Log.d(TAG, "items in cache before refresh: ${cache.value}")
//            val items = ItemApi.itemService.getItems(0, 0)
//            Log.d(TAG, "items returned by API: $items")
//
//            for (item in items) {
//                itemDao.insert(item)
//            }
////            itemsLiveData = itemDao.getAll()
//            Result.Success(true)
//        } catch (ex : Exception) {
//            Result.Error(ex)
//        }
//    }

//    suspend fun getAll() : List<Item> {
//        Log.i(TAG, "loadAll")
//        if (cache != null) {
//            return cache as List<Item>
//        }
//        cache = mutableListOf()
//        val items = ItemApi.itemService.getItems()
//        cache?.addAll(items)
//        return cache as List<Item>
//    }

//    fun getAll() : LiveData<List<Item>> {
//        return cache
//    }

    fun find(id: Int) : LiveData<Item> {
        Log.d(TAG, "find - id: $id")
        return itemDao.getById(id)
    }

    suspend fun save(item: Item) : Result<Item> {
        Log.i(TAG, "save")

        val created: Item

        try {
            created = ItemApi.itemService.createItem(item)
        } catch (ex: Exception) {
//            cache.add(ItemRequest(item, RequestType.SAVE))
            RequestCache.cache.add(ItemRequest(item, RequestType.SAVE))
            itemDao.insert(item)
            return Result.Error(Exception("Item saved to cache!"))
        }
        try {
            itemDao.insert(created)
        } catch (ex: Exception) {
            return Result.Error(ex)
        }
        return Result.Success(created)
    }

    suspend fun update(id: String, item: Item) : Result<Item> {
        Log.i(TAG, "update")

        val updated: Item

        try {
            updated = ItemApi.itemService.updateItem(id, item)
        } catch (ex: Exception) {
//            cache.add(ItemRequest(item, RequestType.UPDATE))
//            if (item.room_id != null)
//                itemReqDao.insert(ItemRequestDb(item.room_id, RequestType.UPDATE))
            RequestCache.cache.add(ItemRequest(item, RequestType.UPDATE))
            itemDao.update(item)
            return Result.Error(Exception("Item saved to cache!"))
        }

        try {
            itemDao.update(updated)
        } catch (ex: Exception) {
            return Result.Error(ex)
        }
        return Result.Success(updated)
    }

    suspend fun addToCache(item: Item) {
        itemDao.insert(item)
    }

    suspend fun synchronize() {
        Log.d(TAG, "synchronizing...")
//        val cache = RequestCache.cache
        Log.d(TAG, "Cache = ${RequestCache.cache}")
        for (itemReq in RequestCache.cache) {
            when (itemReq.type) {
                RequestType.SAVE -> {
                    save(itemReq.item)
                }
                RequestType.UPDATE -> {
                    update(itemReq.item._id, itemReq.item)
                }
                else -> Log.d(TAG, "Unknown request type in synchronize!")
            }
        }

        RequestCache.cache.clear()
    }

}