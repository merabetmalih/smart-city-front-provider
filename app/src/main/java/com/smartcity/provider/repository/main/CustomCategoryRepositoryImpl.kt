package com.smartcity.provider.repository.main

import android.util.Log
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.api.main.responses.CustomCategoryResponse
import com.smartcity.provider.api.main.responses.ListCustomCategoryResponse
import com.smartcity.provider.api.main.responses.ListProductResponse
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.persistence.BlogPostDao
import com.smartcity.provider.repository.buildError
import com.smartcity.provider.repository.safeApiCall
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState.*
import com.smartcity.provider.util.*
import com.smartcity.provider.util.SuccessHandling.Companion.DELETE_DONE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@FlowPreview
@MainScope
class CustomCategoryRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): CustomCategoryRepository {

    private val TAG: String = "AppDebug"

    override fun attemptCustomCategoryMain(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CustomCategoryViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllcustomCategory(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<CustomCategoryViewState, ListCustomCategoryResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListCustomCategoryResponse): DataState<CustomCategoryViewState> {
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
                        data = CustomCategoryViewState(
                            customCategoryFields = CustomCategoryFields(customCategoryList)
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptCreateCustomCategory(
        stateEvent: StateEvent,
        id: Long,
        name: String
    ): Flow<DataState<CustomCategoryViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.createCustomCategory(
                provider = id,
                name = name
            )
        }

        emit(
            object: ApiResponseHandler<CustomCategoryViewState, CustomCategoryResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: CustomCategoryResponse): DataState<CustomCategoryViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_CREATION_DONE,
                            UIComponentType.SnackBar(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptDeleteCustomCategory(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CustomCategoryViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.deleteCustomCategory(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<CustomCategoryViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<CustomCategoryViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    if(resultObj.response == DELETE_DONE){
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

    override fun attemptUpdateCustomCategory(
        stateEvent: StateEvent,
        id: Long,
        name: String,
        provider: Long
    ): Flow<DataState<CustomCategoryViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.updateCustomCategory(
                id = id,
                name = name,
                provider = provider
            )
        }

        emit(
            object: ApiResponseHandler<CustomCategoryViewState, CustomCategoryResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: CustomCategoryResponse): DataState<CustomCategoryViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            UIComponentType.SnackBar(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptCreateProduct(
        stateEvent: StateEvent,
        product: RequestBody,
        productImagesFile: List<MultipartBody.Part>,
        variantsImagesFile: List<MultipartBody.Part>,
        productObject: Product
    ): Flow<DataState<CustomCategoryViewState>> = flow {
        val productFieldsErrors = ProductFields(
            productObject.description,
            productObject.name,
            "",
            "",
            mutableListOf(),
            productObject.productVariants.toMutableList()).isValidForCreation()

        if(!productFieldsErrors.equals(ProductFields.CreateProductError.none())){
            emit(
                buildError<CustomCategoryViewState>(
                    productFieldsErrors,
                    UIComponentType.Dialog(),
                    stateEvent
                )
            )
        }else{
            val apiResult = safeApiCall(Dispatchers.IO){
                openApiMainService.createProduct(
                    product = product,
                    productImagesFile = productImagesFile,
                    variantesImagesFile = variantsImagesFile
                )
            }

            emit(
                object: ApiResponseHandler<CustomCategoryViewState, Product>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: Product): DataState<CustomCategoryViewState> {
                        Log.d(TAG,"handleSuccess ${stateEvent}")

                        return DataState.data(
                            data = null,
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.PRODUCT_CREATION_DONE,
                                UIComponentType.SnackBar(),
                                MessageType.Info()
                            )
                        )
                    }
                }.getResult()
            )
        }
    }

    override fun attemptUpdateProduct(
        stateEvent: StateEvent,
        product: RequestBody,
        productImagesFile: List<MultipartBody.Part>,
        variantsImagesFile: List<MultipartBody.Part>,
        productObject: Product
    ): Flow<DataState<CustomCategoryViewState>> = flow {
        val productFieldsErrors = ProductFields(
            productObject.description,
            productObject.name,
            "",
            "",
            mutableListOf(),
            productObject.productVariants.toMutableList()).isValidForCreation()

        if(!productFieldsErrors.equals(ProductFields.CreateProductError.none())){
            emit(
                buildError<CustomCategoryViewState>(
                    productFieldsErrors,
                    UIComponentType.Dialog(),
                    stateEvent
                )
            )
        }else{
            val apiResult = safeApiCall(Dispatchers.IO){
                openApiMainService.updateProduct(
                    product = product,
                    productImagesFile = productImagesFile,
                    variantesImagesFile = variantsImagesFile
                )
            }

            emit(
                object: ApiResponseHandler<CustomCategoryViewState, Product>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: Product): DataState<CustomCategoryViewState> {
                        Log.d(TAG,"handleSuccess ${stateEvent}")

                        return DataState.data(
                            data = null,
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.PRODUCT_UPDATE_DONE,
                                UIComponentType.SnackBar(),
                                MessageType.Info()
                            )
                        )
                    }
                }.getResult()
            )
        }
    }

    override fun attemptDeleteProduct(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CustomCategoryViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.deleteProduct(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<CustomCategoryViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<CustomCategoryViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    if(resultObj.response == DELETE_DONE){
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

    override fun attemptProductMain(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CustomCategoryViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllProductByCategory(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<CustomCategoryViewState, ListProductResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListProductResponse): DataState<CustomCategoryViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = CustomCategoryViewState(
                            productList = ProductList(resultObj.results)
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptUpdateProductsCustomCategory(
        stateEvent: StateEvent,
        products: List<Long>,
        category: Long
    ): Flow<DataState<CustomCategoryViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.updateProductsCustomCategory(
                products = products,
                category = category
            )
        }

        emit(
            object: ApiResponseHandler<CustomCategoryViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<CustomCategoryViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            UIComponentType.SnackBar(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }
}
















