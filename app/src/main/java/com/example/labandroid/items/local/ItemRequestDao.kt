package com.example.labandroid.items.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.labandroid.items.data.ItemRequestDb

@Dao
interface ItemRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itemReq: ItemRequestDb)

    @Query("DELETE FROM requests")
    suspend fun clearAll()

    @Query("select * from requests")
    suspend fun getItemRequests(): LiveData<List<ItemRequestDb>>
}