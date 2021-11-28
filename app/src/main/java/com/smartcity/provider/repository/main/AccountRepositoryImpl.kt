package com.smartcity.provider.repository.main

import android.content.SharedPreferences
import android.util.Log
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.api.main.responses.ListCustomCategoryResponse
import com.smartcity.provider.api.main.responses.ListGenericResponse
import com.smartcity.provider.api.main.responses.ListProductResponse
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.*
import com.smartcity.provider.repository.buildError
import com.smartcity.provider.repository.safeApiCall
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.util.*
import com.smartcity.provider.util.SuccessHandling.Companion.RESPONSE_GET_NOTIFICATION_SETTINGS_DONE
import com.smartcity.provider.util.SuccessHandling.Companion.RESPONSE_SAVE_NOTIFICATION_SETTINGS_DONE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@MainScope
class AccountRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
): AccountRepository
{
    private val TAG: String = "AppDebug"

    override fun attemptSetNotificationSettings(
        stateEvent: StateEvent,
        settings: List<String>
    ): Flow<DataState<AccountViewState>> = flow {
        Log.d(TAG, "attemptSetNotificationSettings")
        saveNotificationSettings(settings)
        emit(
            DataState.data<AccountViewState>(
                data = null,
                response = Response(
                    RESPONSE_SAVE_NOTIFICATION_SETTINGS_DONE,
                    UIComponentType.Toast(),
                    MessageType.Info()
                ),
                stateEvent = stateEvent
            )
        )
    }

    override fun attemptGetNotificationSettings(
        stateEvent: StateEvent
    ): Flow<DataState<AccountViewState>> = flow {
        val settings = sharedPreferences.getStringSet(PreferenceKeys.NOTIFICATION_SETTINGS, null)
        settings?.let {
            emit(
                DataState.data<AccountViewState>(
                    data = AccountViewState(
                        notificationSettings = it.toList()
                    ),
                    response = Response(
                        RESPONSE_GET_NOTIFICATION_SETTINGS_DONE,
                        UIComponentType.None(),
                        MessageType.None()
                    ),
                    stateEvent = stateEvent
                )
            )
        }
    }

    override fun saveNotificationSettings(settings: List<String>){
        sharedPrefsEditor.putStringSet(PreferenceKeys.NOTIFICATION_SETTINGS,settings.toMutableSet())
        sharedPrefsEditor.apply()
    }

    override fun attemptSetStoreInformation(
        stateEvent: StateEvent,
        storeInformation: StoreInformation
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.setStoreInformation(
                storeInformation = storeInformation
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CREATION_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptCreatePolicy(
        stateEvent: StateEvent,
        policy: Policy
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.createPolicy(
                policy = policy
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CREATION_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetStoreInformation(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getStoreInformation(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, StoreInformation>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: StoreInformation): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = AccountViewState(
                            storeInformationFields= AccountViewState.StoreInformationFields(
                                storeInformation = resultObj
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_STORE_INFORMATION,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptAllCategory(
        stateEvent: StateEvent
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllCategory()
        }

        emit(
            object: ApiResponseHandler<AccountViewState, ListGenericResponse<Category>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<Category>): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = AccountViewState(
                            storeInformationFields = AccountViewState.StoreInformationFields(
                                categoryList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_ALL_CATEGORIES,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetCustomCategories(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllcustomCategory(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, ListCustomCategoryResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListCustomCategoryResponse): DataState<AccountViewState> {
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
                        data = AccountViewState(
                            discountFields = AccountViewState.DiscountFields(
                                customCategoryList
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetProducts(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllProductByCategory(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, ListProductResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListProductResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = AccountViewState(
                            discountFields = AccountViewState.DiscountFields(
                                productsList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptCreateOffer(
        stateEvent: StateEvent,
        offer: Offer
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.createOffer(
                offer = offer
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CREATION_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetOffers(
        stateEvent: StateEvent,
        id: Long,
        offerState: OfferState?
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllOffers(
                id = id,
                status = offerState
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, ListGenericResponse<Offer>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<Offer>): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = AccountViewState(
                            discountOfferList = AccountViewState.DiscountOfferList(
                                offersList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Offers,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptDeleteOffer(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> =flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.deleteOffer(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    if(resultObj.response == SuccessHandling.DELETE_DONE){
                        return DataState.data(
                            data = null,
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.DELETE_DONE,
                                UIComponentType.SnackBar(),
                                MessageType.Info()
                            )
                        )
                    }else{
                        return DataState.error(
                            stateEvent = stateEvent,
                            response = Response(
                                ErrorHandling.ERROR_UNKNOWN,
                                UIComponentType.Dialog(),
                                MessageType.Error()
                            )
                        )
                    }
                }
            }.getResult()
        )
    }

    override fun attemptUpdateOffer(
        stateEvent: StateEvent,
        offer: Offer
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.updateOffer(
                offer = offer
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.PRODUCT_UPDATE_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptCreateFlashDeal(
        stateEvent: StateEvent,
        flashDeal: FlashDeal
    ): Flow<DataState<AccountViewState>> = flow {
        val flashDealErrors = flashDeal.isValidForCreation()

        if(flashDealErrors != FlashDeal.CreateFlashError.none()){
            emit(
                buildError<AccountViewState>(
                    flashDealErrors,
                    UIComponentType.Dialog(),
                    stateEvent
                )
            )
        }else{
            val apiResult = safeApiCall(Dispatchers.IO){
                openApiMainService.createFlashDeal(
                    flashDeal = flashDeal
                )
            }

            emit(
                object: ApiResponseHandler<AccountViewState, GenericResponse>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
                        Log.d(TAG,"handleSuccess ${stateEvent}")

                        return DataState.data(
                            data = null,
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.CREATION_DONE,
                                UIComponentType.Toast(),
                                MessageType.Info()
                            )
                        )
                    }
                }.getResult()
            )
        }
    }

    override fun attemptGetFlashDeals(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getFlashDeals(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, ListGenericResponse<FlashDeal>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<FlashDeal>): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = AccountViewState(
                            flashDealsFields = AccountViewState.FlashDealsFields(
                                flashDealsList= resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Flashes,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetSearchFlashDeals(
        stateEvent: StateEvent,
        id: Long,
        startDate: String?,
        endDate: String?
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getSearchFlashDeals(
                id = id,
                startDate = startDate,
                endDate = endDate
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, ListGenericResponse<FlashDeal>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<FlashDeal>): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = AccountViewState(
                            flashDealsFields = AccountViewState.FlashDealsFields(
                                searchFlashDealsList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Flashes,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }
}