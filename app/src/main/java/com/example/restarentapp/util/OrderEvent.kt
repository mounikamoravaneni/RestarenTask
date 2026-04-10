package com.example.restarentapp.util

sealed interface OrderEvent {
    data class ShowError(val message: String) : OrderEvent
}