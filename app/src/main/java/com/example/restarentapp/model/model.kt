package com.example.restarentapp.model

import androidx.compose.runtime.mutableStateOf

data class OrderResponse(
    val status: Boolean,
    val message: List<OrderData>
)

data class OrderData(
    val order: OrderInfo,
    val item: List<ApiItem>
)

data class OrderInfo(
    val id: Int,
    val total: String
)

data class ApiItem(
    val id: String,
    val item_name: String,
    val price: String,
    val qty: String,
    val finalTotal: String
)

data class Person(
    val name: String,
    val short: String
)

data class ApiError(
    val error: String? = null,
    val message: String? = null
)

data class PersonItemSelection(
    val personIndex: Int,
    val itemId: Int,
    val initialSelected: Boolean = false
) {
    var isSelected = mutableStateOf(initialSelected)
}

// Data class
data class BillItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val total: Double
)
data class OrderRequest(val apiKey:String,val tableId:String)

data class PersonShare(
    val id: String,
    val name: String,
    val shares: Int = 0,
    val selected: Boolean = false
)

data class PersonPercent(
    val id: String,
    val name: String,
    val percent: Int = 0,
    val selected: Boolean = false
)