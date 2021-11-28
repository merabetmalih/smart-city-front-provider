package com.smartcity.provider.ui.main.store.viewmodel

import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.repository.main.StoreRepositoryImpl
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.main.store.state.StoreStateEvent.*
import com.smartcity.provider.ui.main.store.state.StoreViewState
import com.smartcity.provider.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class StoreViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val storeRepository: StoreRepositoryImpl
) : BaseViewModel<StoreViewState>()
{
    override fun handleNewData(data: StoreViewState) {
        data.viewCustomCategoryFields.let { viewCustomCategoryFields ->
            viewCustomCategoryFields.customCategoryList?.let { list ->
                setCustomCategoryList(
                    list
                )
            }
        }

        data.viewProductList.let { viewProductList ->
            viewProductList.products?.let {list ->
                setViewProductList(
                    list
                )
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)){
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<StoreViewState>> = when(stateEvent){

                    is CustomCategoryMainEvent ->{
                        storeRepository.attemptCustomCategoryMain(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is ProductMainEvent ->{
                        storeRepository.attemptProductMain(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is AllProductEvent -> {
                        storeRepository.attemptAllProduct(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    else -> {
                        flow{
                            emit(
                                DataState.error<StoreViewState>(
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

    override fun initNewViewState(): StoreViewState {
        return StoreViewState()
    }
}














