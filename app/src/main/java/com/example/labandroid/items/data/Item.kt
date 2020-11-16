package com.example.labandroid.items.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Entity(tableName = "items")
data class Item(
    @PrimaryKey @ColumnInfo(name = "id") @SerializedName("_id") val _id: String,
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "date") var date: LocalDateTime,
    @ColumnInfo(name = "version") var version: Int
)
{

    override fun toString(): String {
        return "$_id - $text - $date - $version"
    }

}

