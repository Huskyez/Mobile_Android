package com.example.labandroid.items.data

data class ItemEvent(var event: String, var payload: Payload) {

    override fun toString(): String {
        return "$event: $payload"
    }

    inner class Payload(var item: Item) {
        override fun toString(): String {
            return item.toString()
        }
    }
}