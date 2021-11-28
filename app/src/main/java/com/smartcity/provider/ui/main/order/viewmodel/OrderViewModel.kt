package com.smartcity.provider.ui.main.order.viewmodel

import android.content.SharedPreferences
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.repository.main.OrderRepositoryImpl
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.main.order.state.OrderStateEvent.*
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class OrderViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val orderRepository: OrderRepositoryImpl,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
): BaseViewModel<OrderViewState>(){

    override fun handleNewData(data: OrderViewState) {
        data.orderFields.let { orderFields ->
            orderFields.orderList?.let { list ->
                setOrderListData(
                    list
                )
            }

            orderFields.searchOrderList?.let { list ->
                setSearchOrderListData(
                    list
                )
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)){
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<OrderViewState>> = when(stateEvent){

                    is GetOrderEvent -> {
                        orderRepository.attemptGetOrders(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            if (getSelectedSortFilter() == null) "DESC" else (if(getSelectedSortFilter()!!.second == "date") getSelectedSortFilter()!!.third else ""),
                            if (getSelectedSortFilter() == null) "" else (if(getSelectedSortFilter()!!.second == "amount") getSelectedSortFilter()!!.third else ""),
                            getOrderStepFilter(),
                            if (getSelectedTypeFilter() == null) "" else getSelectedTypeFilter()!!.third
                        )
                    }

                    is GetTodayOrderEvent ->{
                        orderRepository.attemptGetTodayOrders(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            if (getSelectedSortFilter() == null) "DESC" else (if(getSelectedSortFilter()!!.second == "date") getSelectedSortFilter()!!.third else ""),
                            if (getSelectedSortFilter() == null) "" else (if(getSelectedSortFilter()!!.second == "amount") getSelectedSortFilter()!!.third else ""),
                            getOrderStepFilter(),
                            if (getSelectedTypeFilter() == null) "" else getSelectedTypeFilter()!!.third
                        )
                    }

                    is GetOrderByDateEvent ->{
                        orderRepository.attemptGetOrdersByDate(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            getRangeDate().first,
                            getRangeDate().second,
                            if (getSelectedSortFilter() == null) "DESC" else (if(getSelectedSortFilter()!!.second == "date") getSelectedSortFilter()!!.third else ""),
                            if (getSelectedSortFilter() == null) "" else (if(getSelectedSortFilter()!!.second == "amount") getSelectedSortFilter()!!.third else ""),
                            getOrderStepFilter(),
                            if (getSelectedTypeFilter() == null) "" else getSelectedTypeFilter()!!.third
                        )
                    }

                    is SetOrderAcceptedEvent ->{
                        orderRepository.attemptSetOrderAccepted(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is SetOrderRejectedEvent ->{
                        orderRepository.attemptSetOrderRejected(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is SetOrderReadyEvent ->{
                        orderRepository.attemptSetOrderReady(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is SetOrderDeliveredEvent ->{
                        orderRepository.attemptSetOrderDelivered(
                            stateEvent,
                            stateEvent.id,
                            stateEvent.comment,
                            stateEvent.date
                        )
                    }

                    is SetOrderPickedUpEvent ->{
                        orderRepository.attemptSetOrderPickedUp(
                            stateEvent,
                            stateEvent.id,
                            stateEvent.comment,
                            stateEvent.date
                        )
                    }

                    is GetOrderByIdEvent ->{
                        orderRepository.attemptGetOrderById(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.orderId
                        )
                    }

                    is SearchOrderByReceiverEvent ->{
                        orderRepository.attemptSearchOrderByReceiver(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.firstName,
                            stateEvent.lastName
                        )
                    }

                    is SearchOrderByDateEvent ->{
                        orderRepository.attemptSearchOrderByDate(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.date
                        )
                    }

                    is SetOrderNoteEvent ->{
                        orderRepository.attemptSetOrderNote(
                            stateEvent,
                            stateEvent.id,
                            stateEvent.note
                        )
                    }

                    is GetPastOrderEvent ->{
                        orderRepository.attemptGetPastOrders(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    else -> {
                        flow{
                            emit(
                                DataState.error<OrderViewState>(
                                    response = Response(
                                        message = ErrorHandling.INVALID_STATE_EVENT,
                                        uiComponentType = UIComponentType.None(),
                                        messageType = MessageType.Error()
                                    ),
                                    stateEvent = stateEvent
                                )
                            )
                        }
                    }
                }
                launchJob(stateEvent, job)
            }
        }
    }

    override fun initNewViewState(): OrderViewState {
        return OrderViewState()
    }
}











