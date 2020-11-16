package com.example.labandroid.items.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item : Item)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: Item)

    @Query("SELECT * FROM items")
    fun getAll() : LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE items.id=:id")
    fun getById(id : String) : LiveData<Item>

    @Query("DELETE FROM items")
    suspend fun deleteAll()

}