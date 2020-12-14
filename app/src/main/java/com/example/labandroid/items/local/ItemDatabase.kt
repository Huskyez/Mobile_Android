package com.example.labandroid.items.local

import android.content.Context
import androidx.room.*
import com.example.labandroid.items.data.Item
import com.example.labandroid.items.data.ItemRequestDb
import com.example.labandroid.items.data.RemoteKeys
import java.time.LocalDateTime

@Database(entities = [Item::class, RemoteKeys::class], version = 3, exportSchema = false)
@TypeConverters(LocalDateTimeConverter::class)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemDao() : ItemDao

    abstract fun remoteKeysDao(): RemoteKeysDao

//    abstract fun itemRequestsDao(): ItemRequestDao

    companion object {

        @Volatile
        private var instance : ItemDatabase? = null

        fun getDatabase(context: Context) : ItemDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "item_database"
                ).fallbackToDestructiveMigration()
                .build()
                instance = newInstance
                return instance as ItemDatabase
            }
        }
    }
}

class LocalDateTimeConverter {

    @TypeConverter
    fun toDate(dateString : String?) : LocalDateTime {
        if (dateString == null) {
            return LocalDateTime.now()
        }
        return LocalDateTime.parse(dateString)
    }

    @TypeConverter
    fun toString(date : LocalDateTime?) : String {
        if (date == null) {
            return LocalDateTime.now().toString()
        }
        return date.toString()
    }

}