package com.smartcity.provider.repository.main

import android.util.Log
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.api.main.responses.ListOrderResponse
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.OrderStep
import com.smartcity.provider.repository.buildError
import com.smartcity.provider.repository.safeApiCall
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.state.OrderViewState.OrderFields
import com.smartcity.provider.util.*
import com.smartcity.provider.util.ErrorHandling.Companion.ERROR_MUST_SELECT_TWO_DATES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@MainScope
class OrderRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val sessionManager: SessionManager
): OrderRepository
{
    private val TAG: String = "AppDebug"

    override fun attemptGetOrders(
        stateEvent: StateEvent,
        id: Long,
        dateFilter: String,
        amountFilter: String,
        orderStep: OrderStep,
        type: String
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllOrders(
                id = id,
                date = dateFilter,
                amount = amountFilter,
                step = orderStep,
                type = type
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, ListOrderResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListOrderResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = OrderViewState(
                            orderFields = OrderFields(
                                orderList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Orders,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetTodayOrders(
        stateEvent: StateEvent,
        id: Long,
        dateFilter: String,
        amountFilter: String,
        orderStep: OrderStep,
        type: String
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getTodayOrders(
                id = id,
                date = dateFilter,
                amount = amountFilter,
                step = orderStep,
                type = type
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, ListOrderResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListOrderResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = OrderViewState(
                            orderFields = OrderFields(
                                orderList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Orders,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetOrdersByDate(
        stateEvent: StateEvent,
        id: Long,
        startDate: String?,
        endDate: String?,
        dateFilter: String,
        amountFilter: String,
        orderStep: OrderStep,
        type: String
    ): Flow<DataState<OrderViewState>> = flow {
        if(startDate.isNullOrEmpty() || endDate.isNullOrEmpty()){
            emit(
                buildError<OrderViewState>(
                    ERROR_MUST_SELECT_TWO_DATES,
                    UIComponentType.Dialog(),
                    stateEvent
                )
            )
        }else{
            val apiResult = safeApiCall(Dispatchers.IO){
                openApiMainService.getOrdersByDate(
                    id= id,
                    startDate = startDate,
                    endDate = endDate,
                    date = dateFilter,
                    amount = amountFilter,
                    step = orderStep,
                    type = type
                )
            }

            emit(
                object: ApiResponseHandler<OrderViewState, ListOrderResponse>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: ListOrderResponse): DataState<OrderViewState> {
                        Log.d(TAG,"handleSuccess ${stateEvent}")

                        return DataState.data(
                            data = OrderViewState(
                                orderFields = OrderFields(
                                    orderList = resultObj.results
                                )
                            ),
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.DONE_Orders,
                                UIComponentType.None(),
                                MessageType.None()
                            )
                        )
                    }
                }.getResult()
            )
        }
    }

    override fun attemptSetOrderAccepted(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.setOrderAccepted(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptSetOrderRejected(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.setOrderRejected(
                id= id
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptSetOrderReady(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.setOrderReady(
                id= id
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptSetOrderDelivered(
        stateEvent: StateEvent,
        id: Long,
        comment: String?,
        date: String?
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.setOrderDelivered(
                id = id,
                comment = comment,
                date = date
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptSetOrderPickedUp(
        stateEvent: StateEvent,
        id: Long,
        comment: String?,
        date: String?
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.setOrderPickedUp(
                id = id,
                comment = comment,
                date = date
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetOrderById(
        stateEvent: StateEvent,
        id: Long,
        orderId: Long
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getOrderById(
                id = id,
                orderId = orderId
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, ListOrderResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListOrderResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = OrderViewState(
                            orderFields = OrderFields(
                                searchOrderList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Order,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptSearchOrderByReceiver(
        stateEvent: StateEvent,
        id: Long,
        firstName: String,
        lastName: String
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.searchOrderByReceiver(
                id = id,
                firstName = firstName,
                lastName = lastName
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, ListOrderResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListOrderResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = OrderViewState(
                            orderFields = OrderFields(
                                searchOrderList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Order,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptSearchOrderByDate(
        stateEvent: StateEvent,
        id: Long,
        date: String
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.searchOrderByDate(
                id = id,
                date = date
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, ListOrderResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListOrderResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = OrderViewState(
                            orderFields = OrderFields(
                                searchOrderList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Order,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptSetOrderNote(
        stateEvent: StateEvent,
        id: Long,
        note: String
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.setOrderNote(
                id = id,
                note = note
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CREATION_DONE,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetPastOrders(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<OrderViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getPastOrders(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<OrderViewState, ListOrderResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListOrderResponse): DataState<OrderViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = OrderViewState(
                            orderFields = OrderFields(
                                searchOrderList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Order,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }
}
















