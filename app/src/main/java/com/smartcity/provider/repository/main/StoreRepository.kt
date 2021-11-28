package com.smartcity.provider.repository.main

import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.ui.main.store.state.StoreViewState
import com.smartcity.provider.util.DataState
import com.smartcity.provider.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@MainScope
interface StoreRepository {

    fun attemptCustomCategoryMain(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<StoreViewState>>
    
    fun attemptProductMain(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<StoreViewState>>
    
    fun attemptAllProduct(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<StoreViewState>>
}