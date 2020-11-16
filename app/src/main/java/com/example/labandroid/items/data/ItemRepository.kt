package com.example.labandroid.items.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.labandroid.items.api.ItemApi
import com.example.labandroid.utils.TAG
import com.example.labandroid.utils.Result
import java.lang.Exception

class ItemRepository(private val itemDao : ItemDao) {

//    private var cache : MutableList<Item>? = null
//    val cache = MediatorLiveData<List<Item>>()
//    var itemsLiveData = itemDao.getAll()
    val cache = itemDao.getAll()

//    init {
//        cache.addSource(itemsLiveData) {
//            cache.removeSource(itemsLiveData)
//            cache.value = itemsLiveData.value
//            val list : MutableList<Item>? = cache.value?.toMutableList()
//            val listToAdd = itemsLiveData.value?.filter { item -> cache.value?.find { it.id == item.id } == null }
//
//            if (listToAdd != null) {
//                for (item in listToAdd) {
//                    list?.add(item)
//                }
//            }
//            cache.value = list
//            Log.d(TAG, "items in cache: ${cache.value}")
//        }
//    }

    suspend fun refresh() : Result<Boolean> {
        return try {

            Log.d(TAG, "items in cache before refresh: ${cache.value}")
            val items = ItemApi.itemService.getItems()
            Log.d(TAG, "items returned by API: $items")
            itemDao.deleteAll()
//            for (item in items) {
//                if (itemDao.getById(item.id).value == null) {
//                    Log.d(TAG, "item with id: ${item.id} was not found")
//                    itemDao.insert(item)
//                } else {
//                    Log.d(TAG, "item with id: ${item.id} was found")
//                }
//            }

            for (item in items) {
                itemDao.insert(item)
            }
//            itemsLiveData = itemDao.getAll()
            Result.Success(true)
        } catch (ex : Exception) {
            Result.Error(ex)
        }
    }

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

    fun getAll() : LiveData<List<Item>> {
        return cache
    }

    fun find(id: String) : LiveData<Item> {
        Log.d(TAG, "find - id: $id")
        return itemDao.getById(id)
    }

    suspend fun save(item: Item) : Result<Item> {
        Log.i(TAG, "save")
        return try {
            val created = ItemApi.itemService.createItem(item)
            itemDao.insert(created)

            Result.Success(created)
        } catch (ex : Exception) {
            Result.Error(ex)
        }
    }

    suspend fun update(id: String, item: Item) : Result<Item> {
        Log.i(TAG, "update")
        return try {
            val updated = ItemApi.itemService.updateItem(id, item)
            itemDao.update(updated)

            Result.Success(updated)
        } catch (ex : Exception) {
            Result.Error(ex)
        }
    }

    suspend fun addToCache(item: Item) {
        itemDao.insert(item)
    }
}