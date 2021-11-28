package com.smartcity.provider.repository.main

import android.util.Log
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.api.main.responses.ListCustomCategoryResponse
import com.smartcity.provider.api.main.responses.ListProductResponse
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.persistence.AccountPropertiesDao
import com.smartcity.provider.repository.safeApiCall
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.main.store.state.StoreViewState
import com.smartcity.provider.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@MainScope
class StoreRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
): StoreRepository
{
    private val TAG: String = "AppDebug"


    override fun attemptCustomCategoryMain(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<StoreViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllcustomCategory(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<StoreViewState, ListCustomCategoryResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListCustomCategoryResponse): DataState<StoreViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    val customCategoryList: ArrayList<CustomCategory> = ArrayList()
                    for (customCategoryResponse in resultObj.results) {
                        customCategoryList.add(
                            CustomCategory(
                                pk = customCategoryResponse.pk,
                                name = customCategoryResponse.name,
                                provider = customCategoryResponse.provider
                            )
                        )
                    }

                    return DataState.data(
                        data = StoreViewState(
                            viewCustomCategoryFields = StoreViewState.ViewCustomCategoryFields(customCategoryList)
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Custom_Category_Main,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptProductMain(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<StoreViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllProductByCategory(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<StoreViewState, ListProductResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListProductResponse): DataState<StoreViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = StoreViewState(
                            viewProductList = StoreViewState.ViewProductList(resultObj.results)
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Product_Main,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptAllProduct(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<StoreViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllProduct(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<StoreViewState, ListProductResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListProductResponse): DataState<StoreViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = StoreViewState(
                            viewProductList = StoreViewState.ViewProductList(resultObj.results)
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_All_Product,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }
}












