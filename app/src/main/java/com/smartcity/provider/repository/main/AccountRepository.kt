package com.smartcity.provider.repository.main

import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.*
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.util.DataState
import com.smartcity.provider.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@MainScope
interface AccountRepository {
    
    fun attemptSetNotificationSettings(
        stateEvent: StateEvent,
        settings: List<String>
    ): Flow<DataState<AccountViewState>>
    
    fun attemptGetNotificationSettings(
        stateEvent: StateEvent,
    ): Flow<DataState<AccountViewState>>
    
    fun saveNotificationSettings(
        settings: List<String>
    )

    fun attemptCreatePolicy(
        stateEvent: StateEvent,
        policy: Policy
    ): Flow<DataState<AccountViewState>>
    
    fun attemptSetStoreInformation(
        stateEvent: StateEvent,
        storeInformation: StoreInformation
    ): Flow<DataState<AccountViewState>>
    
    fun attemptGetStoreInformation(
        stateEvent: StateEvent,
        id:Long
    ): Flow<DataState<AccountViewState>>
    
    fun attemptAllCategory(
        stateEvent: StateEvent,
    ): Flow<DataState<AccountViewState>>
    
    fun attemptGetCustomCategories(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>>
    
    fun attemptGetProducts(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>>
    
    fun attemptCreateOffer(
        stateEvent: StateEvent,
        offer: Offer
    ): Flow<DataState<AccountViewState>>
    
    fun attemptGetOffers(
        stateEvent: StateEvent,
        id:Long,
        offerState: OfferState?
    ): Flow<DataState<AccountViewState>>
    
    fun attemptDeleteOffer(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>>
    
    fun attemptUpdateOffer(
        stateEvent: StateEvent,
        offer: Offer
    ): Flow<DataState<AccountViewState>>
    
    fun attemptCreateFlashDeal(
        stateEvent: StateEvent,
        flashDeal: FlashDeal
    ): Flow<DataState<AccountViewState>>
    
    fun attemptGetFlashDeals(
        stateEvent: StateEvent,
        id:Long
    ): Flow<DataState<AccountViewState>>
    
    fun attemptGetSearchFlashDeals(
        stateEvent: StateEvent,
        id:Long,
        startDate: String?,
        endDate: String?
    ): Flow<DataState<AccountViewState>>
}