package com.smartcity.provider.repository.config

import com.smartcity.provider.di.config.ConfigScope
import com.smartcity.provider.ui.config.state.ConfigViewState
import com.smartcity.provider.util.DataState
import com.smartcity.provider.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

@FlowPreview
@ConfigScope
interface ConfigRepository {
    
    fun attemptCreateStore(
        stateEvent: StateEvent,
        store: RequestBody,
        image: MultipartBody.Part?
    ): Flow<DataState<ConfigViewState>>
    
    fun attemptGetStoreCategories(
        stateEvent: StateEvent,
        id : Long
    ): Flow<DataState<ConfigViewState>>
    
    fun attemptSetStoreCategories(
        stateEvent: StateEvent,
        id : Long,
        categories : List<String>
    ): Flow<DataState<ConfigViewState>>
    
    fun attemptGetAllCategories(
        stateEvent: StateEvent,
    ): Flow<DataState<ConfigViewState>>
}