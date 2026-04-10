package com.example.restarentapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restarentapp.model.BillItem
import com.example.restarentapp.model.OrderRequest
import com.example.restarentapp.repository.OrderRepository
import com.example.restarentapp.util.ApiResult
import com.example.restarentapp.util.OrderEvent
import com.example.restarentapp.util.OrderUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val repository: OrderRepository): ViewModel() {

    // ✅ StateFlow (SSOT)
    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState

    // ✅ SharedFlow (one-time events)
    private val _event = MutableSharedFlow<OrderEvent>()
    val event: SharedFlow<OrderEvent> = _event
    init{
        getTableData()
    }

    fun getTableData() {
        viewModelScope.launch {

            _uiState.value = OrderUiState.Loading

            when (val result = repository.getTableData(
                OrderRequest("IXGZ1Y4DQ836QGFN", "25")
            )) {

                is ApiResult.Success -> {

                    val response = result.data   // ✅ NOW you get OrderResponse

                    val items = response.message.firstOrNull()?.item?.map {
                        BillItem(
                            id = it.id,
                            name = it.item_name,
                            price = it.price.toDouble(),
                            quantity = it.qty.toInt(),
                            total = it.finalTotal.toDouble()
                        )
                    } ?: emptyList()

                    val total = items.sumOf { it.total }

                    _uiState.value = OrderUiState.Success(items, total)
                    Log.d("response","-->${items},...${total}")
                }

                is ApiResult.Error -> {

                    _uiState.value = OrderUiState.Error(result.message)

                    _event.emit(OrderEvent.ShowError(result.message))
                }
            }
        }
    }

}