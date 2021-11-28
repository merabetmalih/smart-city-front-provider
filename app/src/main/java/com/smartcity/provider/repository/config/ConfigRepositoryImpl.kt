package com.smartcity.provider.repository.config

import android.content.SharedPreferences
import android.util.Log
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.auth.network_responses.StoreResponse
import com.smartcity.provider.api.config.OpenApiConfigService
import com.smartcity.provider.api.main.responses.ListGenericResponse
import com.smartcity.provider.di.config.ConfigScope
import com.smartcity.provider.models.Category
import com.smartcity.provider.repository.safeApiCall
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.config.state.ConfigViewState
import com.smartcity.provider.ui.config.state.StoreFields
import com.smartcity.provider.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@FlowPreview
@ConfigScope
class ConfigRepositoryImpl
@Inject
constructor(
    val openApiConfigService: OpenApiConfigService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
): ConfigRepository
{
    private val TAG: String = "AppDebug"

    override fun attemptCreateStore(
        stateEvent: StateEvent,
        store: RequestBody,
        image: MultipartBody.Part?
    ): Flow<DataState<ConfigViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiConfigService.createStore(
                store = store,
                image = image
            )
        }

        emit(
            object: ApiResponseHandler<ConfigViewState, StoreResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: StoreResponse): DataState<ConfigViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.STORE_CREATION_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }

            }.getResult()
        )
    }

    override fun attemptGetStoreCategories(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<ConfigViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiConfigService.getStoreCategories(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<ConfigViewState, ListGenericResponse<Category>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<Category>): DataState<ConfigViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = ConfigViewState(
                            storeFields = StoreFields(
                                storeCategory = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }

            }.getResult()
        )
    }

    override fun attemptSetStoreCategories(
        stateEvent: StateEvent,
        id: Long,
        categories: List<String>
    ): Flow<DataState<ConfigViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiConfigService.setStoreCategories(
                id = id,
                categories = categories
            )
        }

        emit(
            object: ApiResponseHandler<ConfigViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<ConfigViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    sharedPrefsEditor.putBoolean(PreferenceKeys.CREATE_STORE_FLAG,true)
                    sharedPrefsEditor.apply()

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.STORE_CATEGORIES_DONE,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }

            }.getResult()
        )
    }

    override fun attemptGetAllCategories(
        stateEvent: StateEvent
    ): Flow<DataState<ConfigViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiConfigService.getAllCategories()
        }

        emit(
            object: ApiResponseHandler<ConfigViewState, ListGenericResponse<Category>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<Category>): DataState<ConfigViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = ConfigViewState(
                            storeFields = StoreFields(
                                allCategoryStore = resultObj.results
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
}