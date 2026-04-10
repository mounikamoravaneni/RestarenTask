package com.example.restarentapp.network

import com.example.restarentapp.model.OrderRequest
import com.example.restarentapp.model.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {

    @POST("getOrderByTableId")
    suspend fun getTableOrder(@Body request :OrderRequest) : Response<OrderResponse>
}