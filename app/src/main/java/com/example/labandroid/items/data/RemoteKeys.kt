package com.example.labandroid.items.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKeys(@PrimaryKey val repoId: String, val prevKey: Int?, val nextKey: Int?) {
}