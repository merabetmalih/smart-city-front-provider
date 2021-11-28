package com.smartcity.provider.ui.main.custom_category.viewmodel

import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.repository.main.CustomCategoryRepositoryImpl
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryStateEvent.*
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class CustomCategoryViewModel
@Inject
constructor(
    val customCategoryRepository: CustomCategoryRepositoryImpl,
    val sessionManager: SessionManager
): BaseViewModel<CustomCategoryViewState>() {

    override fun handleNewData(data: CustomCategoryViewState) {
        data.customCategoryFields.let { customCategoryFields ->
            customCategoryFields.customCategoryList?.let {list ->
                setCustomCategoryList(
                    list
                )
            }
        }

        data.productList.let { productList ->
            productList.products?.let {list ->
                setProducts(
                    list
                )
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)){
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<CustomCategoryViewState>> = when(stateEvent){

                    is CustomCategoryMainEvent ->{
                        customCategoryRepository.attemptCustomCategoryMain(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is CreateCustomCategoryEvent ->{
                        customCategoryRepository.attemptCreateCustomCategory(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.name
                        )
                    }

                    is DeleteCustomCategoryEvent -> {
                        customCategoryRepository.attemptDeleteCustomCategory(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is UpdateCustomCategoryEvent -> {
                        customCategoryRepository.attemptUpdateCustomCategory(
                            stateEvent,
                            stateEvent.id,
                            stateEvent.name,
                            stateEvent.provider
                        )
                    }

                    is CreateProductEvent ->{
                        customCategoryRepository.attemptCreateProduct(
                            stateEvent,
                            stateEvent.product,
                            stateEvent.productImagesFile,
                            stateEvent.variantesImagesFile,
                            stateEvent.productObject
                        )
                    }

                    is UpdateProductEvent ->{
                        customCategoryRepository.attemptUpdateProduct(
                            stateEvent,
                            stateEvent.product,
                            stateEvent.productImagesFile,
                            stateEvent.variantesImagesFile,
                            stateEvent.productObject
                        )
                    }

                    is DeleteProductEvent ->{
                        customCategoryRepository.attemptDeleteProduct(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is ProductMainEvent ->{
                        customCategoryRepository.attemptProductMain(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is UpdateProductsCustomCategoryEvent ->{
                        customCategoryRepository.attemptUpdateProductsCustomCategory(
                            stateEvent,
                            stateEvent.products,
                            stateEvent.category
                        )
                    }

                    else -> {
                        flow{
                            emit(
                                DataState.error<CustomCategoryViewState>(
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

    override fun initNewViewState(): CustomCategoryViewState {
        return CustomCategoryViewState()
    }
}










