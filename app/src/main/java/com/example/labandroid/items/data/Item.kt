package com.example.labandroid.items.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Entity(tableName = "items", indices = [Index(value = ["_id"], unique = true)])
data class Item(
    @ColumnInfo(name = "_id") @SerializedName("_id") val _id: String,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "room_id") @Expose(serialize = false, deserialize = false)
    val room_id: Int?,
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "date") var date: LocalDateTime,
    @ColumnInfo(name = "version") var version: Int
)
{

    override fun toString(): String {
        return "$_id - $text - $date - $version"
    }

    override fun equals(other: Any?): Boolean {
        if (other is Item) {
            return other._id == _id
        }
        return false
    }
}

