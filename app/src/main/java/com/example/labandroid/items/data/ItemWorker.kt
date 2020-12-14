package com.example.labandroid.items.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.labandroid.items.local.ItemDatabase
import com.example.labandroid.utils.TAG

class ItemWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        Log.d(TAG, "Do Work")
        val itemRepo = ItemRepository(ItemDatabase.getDatabase(context).itemDao())
//        val itemReqDao = ItemDatabase.getDatabase(context).itemRequestsDao()


        itemRepo.synchronize()
        return Result.success()
    }
}