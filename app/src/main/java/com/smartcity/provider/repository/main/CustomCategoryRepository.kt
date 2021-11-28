package com.smartcity.provider.repository.main

import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.util.DataState
import com.smartcity.provider.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

@FlowPreview
@MainScope
interface CustomCategoryRepository {

    fun attemptCustomCategoryMain(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CustomCategoryViewState>>
    
    fun attemptCreateCustomCategory(
        stateEvent: StateEvent,
        id: Long,
        name :String
    ): Flow<DataState<CustomCategoryViewState>>
    
    fun attemptDeleteCustomCategory(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CustomCategoryViewState>>
    
    fun attemptUpdateCustomCategory(
        stateEvent: StateEvent,
        id: Long,
        name :String,
        provider:Long
    ): Flow<DataState<CustomCategoryViewState>>
    
    fun attemptCreateProduct(
        stateEvent: StateEvent,
        product: RequestBody,
        productImagesFile: List<MultipartBody.Part>,
        variantsImagesFile : List<MultipartBody.Part>,
        productObject: Product
    ): Flow<DataState<CustomCategoryViewState>>
    
    fun attemptUpdateProduct(
        stateEvent: StateEvent,
        product: RequestBody,
        productImagesFile: List<MultipartBody.Part>,
        variantsImagesFile : List<MultipartBody.Part>,
        productObject: Product
    ): Flow<DataState<CustomCategoryViewState>>
    
    fun attemptDeleteProduct(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CustomCategoryViewState>>
    
    fun attemptProductMain(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CustomCategoryViewState>>
    
    fun attemptUpdateProductsCustomCategory(
        stateEvent: StateEvent,
        products: List<Long>,
        category: Long
    ): Flow<DataState<CustomCategoryViewState>>
}