package com.melitopolcherry.hackathon.data.models.order

import com.google.gson.annotations.SerializedName

data class GetOrdersResponse(

    @field:SerializedName("total_elements")
    val totalElements: Int? = null,

    @field:SerializedName("orders")
    val orders: List<OrderItem>? = null,

    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("total_pages")
    val totalPages: Int? = null,

    @field:SerializedName("page_size")
    val pageSize: Int? = null
)