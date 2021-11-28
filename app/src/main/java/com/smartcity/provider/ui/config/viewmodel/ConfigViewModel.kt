package com.smartcity.provider.ui.config.viewmodel

import com.google.gson.Gson
import com.smartcity.provider.di.config.ConfigScope
import com.smartcity.provider.repository.config.ConfigRepositoryImpl
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.config.state.ConfigStateEvent
import com.smartcity.provider.ui.config.state.ConfigViewState
import com.smartcity.provider.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@ConfigScope
class ConfigViewModel
@Inject
constructor(
    val configRepository: ConfigRepositoryImpl,
    private val sessionManager: SessionManager
): BaseViewModel<ConfigViewState>()
{
    override fun handleNewData(data: ConfigViewState) {
        data.storeFields.let { storeFields ->
            storeFields.storeCategory?.let {list ->
                setStoreCategories(
                    list
                )
            }

            storeFields.allCategoryStore?.let { list ->
                setListAllCategoryStore(
                    list
                )
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)){
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<ConfigViewState>> = when(stateEvent){

                    is ConfigStateEvent.CreateStoreAttemptEvent ->{
                        stateEvent.store.provider=authToken.account_pk!!.toLong()
                        val gson = Gson()
                        val productJson: String = gson.toJson(stateEvent.store)
                        val requestBody= RequestBody.create(
                            MediaType.parse("application/json"),
                            productJson
                        )
                        configRepository.attemptCreateStore(
                            stateEvent,
                            requestBody,
                            stateEvent.image
                        )
                    }

                    is ConfigStateEvent.GetStoreCategoriesEvent ->{
                        configRepository.attemptGetStoreCategories(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is ConfigStateEvent.SetStoreCategoriesEvent ->{
                        configRepository.attemptSetStoreCategories(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.categories
                        )
                    }

                    is ConfigStateEvent.GetAllCategoriesEvent ->{
                        configRepository.attemptGetAllCategories(
                            stateEvent
                        )
                    }

                    else -> {
                        flow{
                            emit(
                                DataState.error<ConfigViewState>(
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

    override fun initNewViewState(): ConfigViewState {
        return ConfigViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}