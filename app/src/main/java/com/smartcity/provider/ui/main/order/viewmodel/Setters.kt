package com.smartcity.provider.ui.main.order.viewmodel

import com.smartcity.provider.models.OrderStep
import com.smartcity.provider.models.product.Order
import com.smartcity.provider.ui.main.store.state.StoreViewState


fun OrderViewModel.setOrderListData(orderList: List<Order>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderList = orderList
    setViewState(update)
}

fun OrderViewModel.setSearchOrderListData(orderList: List<Order>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.searchOrderList = orderList
    setViewState(update)
}

fun OrderViewModel.setSelectedOrder(order: Order?){
    val update = getCurrentViewStateOrNew()
    update.orderFields.selectedOrder = order
    setViewState(update)
}

fun OrderViewModel.setOrderActionList(orderActionList: List<Triple<String,Int,Int>>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderAction = orderActionList
    setViewState(update)
}

fun OrderViewModel.setOrderStepsList(orderStepsList: List<Pair<String,Int>>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderSteps = orderStepsList
    setViewState(update)
}

fun OrderViewModel.setOrderActionRecyclerPosition(postion:Int){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderActionRecyclerPosition=postion
    setViewState(update)
}

fun OrderViewModel.setOrderStepsRecyclerPosition(postion:Int){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderStepsRecyclerPosition=postion
    setViewState(update)
}

fun OrderViewModel.clearOrderList(){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderList= listOf()
    setViewState(update)
}

fun OrderViewModel.setSelectedTypeFilter(value: Triple<String,String,String>?){
    val update = getCurrentViewStateOrNew()
    update.orderFields.selectedTypeFilter = value
    setViewState(update)
}

fun OrderViewModel.setSelectedSortFilter(value: Triple<String,String,String>?){
    val update = getCurrentViewStateOrNew()
    update.orderFields.selectedSortFilter = value
    setViewState(update)
}

fun OrderViewModel.setOrderStepFilter(filter: OrderStep?){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderStepFilter = filter!!
    setViewState(update)
}

fun OrderViewModel.setRangeDate(range: Pair<String?,String?>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.rangeDate = range
    setViewState(update)
}