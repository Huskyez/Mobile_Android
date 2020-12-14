package com.example.labandroid.items.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.labandroid.items.data.Item


@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item : Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items : List<Item>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: Item)

    @Query("select * from items order by items.room_id asc")
    fun getAll() : LiveData<List<Item>>

    @Query("select * from items where items.room_id=:id")
    fun getById(id : Int) : LiveData<Item>

    @Query("select * from items order by items.room_id asc")
    fun pagingSource() : PagingSource<Int, Item>

    @Query("DELETE FROM items")
    suspend fun clearAll()

}