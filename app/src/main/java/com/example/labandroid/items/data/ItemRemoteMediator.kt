package com.example.labandroid.items.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.labandroid.items.api.ItemApi
import com.example.labandroid.items.local.ItemDatabase
import com.example.labandroid.utils.PaginationData
import com.example.labandroid.utils.TAG
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

@OptIn(ExperimentalPagingApi::class)
class ItemRemoteMediator(
//    private val query: String,
    private val database: ItemDatabase,
    private val networkService: ItemApi.ApiService
) : RemoteMediator<Int, Item>() {

    private val itemDao = database.itemDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Item>
    ): MediatorResult {

        return try {
            val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
                is MediatorResult.Success -> {
                    Log.d(TAG, "EndOfPagination")
                    return pageKeyData
                }
                else -> {
                    pageKeyData as Int
                }
            }

            val response = networkService.getItems(page, PaginationData.pageSize)

            Log.d(TAG, "page=$page, loadType=$loadType")

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    itemDao.clearAll()
                    remoteKeysDao.clearRemoteKeys()
//                    Log.d(TAG, "loadType=$loadType")
                }

                val isEndOfList = response.size < PaginationData.pageSize

                Log.d(TAG, "Response: $response")
                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.map {
                    RemoteKeys(repoId = it._id, prevKey = prevKey, nextKey = nextKey)
                }
                remoteKeysDao.insertAll(keys)
                itemDao.insertAll(response)
            }

            if (response.size < PaginationData.pageSize) {
                Log.d(TAG, "Reached end of pagination")
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            MediatorResult.Success(endOfPaginationReached = false)
        } catch (e : IOException) {
            MediatorResult.Error(e)
        } catch (e : HttpException) {
            MediatorResult.Error(e)
        }

    }

    private suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, Item>): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: 0
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                    ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                remoteKeys.nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                    ?: throw InvalidObjectException("Invalid state, key should not be null")
                //end of list condition reached
                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.prevKey
            }
        }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, Item>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { item -> database.remoteKeysDao().remoteKeysItem(item._id) }
    }

    /**
     * get the first remote key inserted which had the data
     */
    private suspend fun getFirstRemoteKey(state: PagingState<Int, Item>): RemoteKeys? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { item -> database.remoteKeysDao().remoteKeysItem(item._id) }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, Item>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?._id?.let { repoId ->
                database.remoteKeysDao().remoteKeysItem(repoId)
            }
        }
    }
}