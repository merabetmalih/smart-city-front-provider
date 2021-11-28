package com.smartcity.provider.ui.main.order.state

import android.os.Parcelable
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.OrderStep
import com.smartcity.provider.models.product.Order
import com.smartcity.provider.models.product.OrderType
import kotlinx.android.parcel.Parcelize

const val ORDER_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.blog.state.OrderViewState"

@Parcelize
data class OrderViewState (

    var orderFields: OrderFields = OrderFields()

): Parcelable {

    @Parcelize
    data class OrderFields(
        var searchOrderList: List<Order>? = null,
        var orderList: List<Order>? = null,
        var selectedOrder:Order?=null,
        var orderAction: List<Triple<String,Int,Int>>? = null,
        var orderSteps: List<Pair<String,Int>>? = null,
        var orderActionRecyclerPosition: Int? = null,
        var orderStepsRecyclerPosition: Int? = null,
        var selectedSortFilter:Triple<String,String,String> ?=null,
        var selectedTypeFilter:Triple<String,String,String> ?=null,
        var rangeDate:Pair<String?,String?> =Pair(null,null),
        var orderStepFilter: OrderStep? = null
    ) : Parcelable
}








