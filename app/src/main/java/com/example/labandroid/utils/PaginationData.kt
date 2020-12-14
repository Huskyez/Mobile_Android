package com.example.labandroid.utils

data class PaginationData (val skip : Int?, val limit : Int?) {
    companion object {
        const val pageSize = 5
    }
}