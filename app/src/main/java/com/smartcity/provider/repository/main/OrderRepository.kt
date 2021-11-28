package com.smartcity.provider.repository.main

import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.OrderStep
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.util.DataState
import com.smartcity.provider.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@MainScope
interface OrderRepository {
    
    fun attemptGetOrders(
        stateEvent: StateEvent,
        id:Long,
        dateFilter:String,
        amountFilter:String,
        orderStep: OrderStep,
        type: String
    ): Flow<DataState<OrderViewState>>

    fun attemptGetTodayOrders(
        stateEvent: StateEvent,
        id:Long,
        dateFilter:String,
        amountFilter:String,
        orderStep: OrderStep,
        type: String
    ): Flow<DataState<OrderViewState>>

    fun attemptGetOrdersByDate(
        stateEvent: StateEvent,
        id:Long,
        startDate: String?,
        endDate: String?,
        dateFilter:String,
        amountFilter:String,
        orderStep: OrderStep,
        type: String
    ): Flow<DataState<OrderViewState>>

    fun attemptSetOrderAccepted(
        stateEvent: StateEvent,
        id:Long
    ): Flow<DataState<OrderViewState>>

    fun attemptSetOrderRejected(
        stateEvent: StateEvent,
        id:Long
    ): Flow<DataState<OrderViewState>>

    fun attemptSetOrderReady(
        stateEvent: StateEvent,
        id:Long
    ): Flow<DataState<OrderViewState>>

    fun attemptSetOrderDelivered(
        stateEvent: StateEvent,
        id:Long,
        comment:String?,
        date:String?
    ): Flow<DataState<OrderViewState>>

    fun attemptSetOrderPickedUp(
        stateEvent: StateEvent,
        id:Long,
        comment:String?,
        date:String?
    ): Flow<DataState<OrderViewState>>

    fun attemptGetOrderById(
        stateEvent: StateEvent,
        id:Long,
        orderId:Long
    ): Flow<DataState<OrderViewState>>

    fun attemptSearchOrderByReceiver(
        stateEvent: StateEvent,
        id:Long,
        firstName :String,
        lastName :String
    ): Flow<DataState<OrderViewState>>

    fun attemptSearchOrderByDate(
        stateEvent: StateEvent,
        id:Long,
        date :String
    ): Flow<DataState<OrderViewState>>

    fun attemptSetOrderNote(
        stateEvent: StateEvent,
        id:Long,
        note :String
    ): Flow<DataState<OrderViewState>>

    fun attemptGetPastOrders(
        stateEvent: StateEvent,
        id:Long
    ): Flow<DataState<OrderViewState>>
}