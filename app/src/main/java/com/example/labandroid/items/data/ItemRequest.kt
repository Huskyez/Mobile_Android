package com.example.labandroid.items.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class RequestType {
    SAVE, UPDATE, REMOVE
}

@Entity(tableName = "requests", foreignKeys = [ForeignKey(entity = Item::class, parentColumns = ["room_id"], childColumns = ["room_id"])])
class ItemRequestDb(
    @PrimaryKey @ColumnInfo(name = "room_id") val room_id: Int,
    @ColumnInfo(name = "type") val type: RequestType
    )


class ItemRequest(val item: Item, val type: RequestType)