package com.example.restarentapp.util

import com.example.restarentapp.model.BillItem

sealed interface OrderUiState {

    object Loading : OrderUiState

    data class Success(
        val items: List<BillItem>,
        val totalAmount: Double
    ) : OrderUiState

    data class Error(
        val message: String
    ) : OrderUiState
}