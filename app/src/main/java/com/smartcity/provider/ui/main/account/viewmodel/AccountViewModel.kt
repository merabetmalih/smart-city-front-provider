package com.smartcity.provider.ui.main.account.viewmodel

import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.repository.main.AccountRepositoryImpl
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.main.account.state.AccountStateEvent.*
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepositoryImpl
) : BaseViewModel<AccountViewState>()
{

    override fun handleNewData(data: AccountViewState) {
        data.notificationSettings?.let { list ->
            setNotificationSettings(
                list
            )
        }

        data.storeInformationFields.let { storeInformationFields ->
            storeInformationFields.storeInformation?.let {
                setStoreInformation(
                    it
                )
                setSelectedCategoriesList(
                    it.defaultCategoriesList!!.toMutableList()
                )
            }

            storeInformationFields.categoryList?.let { list ->
                setCategoriesList(
                    list
                )
            }
        }

        data.discountFields.let { discountFields ->
            discountFields.customCategoryList?.let { list ->
                setCustomCategoryList(
                    list
                )
            }

            discountFields.productsList?.let { list ->
                setProductList(
                    list
                )
            }
        }

        data.discountOfferList.let { discountOfferList ->
            discountOfferList.offersList?.let { list ->
                setOffersList(
                    list
                )
            }
        }

        data.flashDealsFields.let { flashDealsFields ->
            flashDealsFields.flashDealsList?.let {list ->
                setFlashDealsList(
                    list
                )
            }

            flashDealsFields.searchFlashDealsList?.let { list ->
                setSearchFlashDealsList(
                    list
                )
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)){
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<AccountViewState>> = when(stateEvent){

                    is GetNotificationSettingsEvent ->{
                        accountRepository.attemptGetNotificationSettings(
                            stateEvent
                        )
                    }

                    is SaveNotificationSettingsEvent ->{
                         accountRepository.attemptSetNotificationSettings(
                             stateEvent,
                            stateEvent.settings
                        )
                    }

                    is SavePolicyEvent ->{
                        stateEvent.policy.providerId=authToken.account_pk!!.toLong()
                        accountRepository.attemptCreatePolicy(
                            stateEvent,
                            stateEvent.policy
                        )
                    }

                    is SetStoreInformationEvent ->{
                        stateEvent.storeInformation.providerId=authToken.account_pk!!.toLong()
                        accountRepository.attemptSetStoreInformation(
                            stateEvent,
                            stateEvent.storeInformation
                        )
                    }

                    is GetStoreInformationEvent ->{
                        accountRepository.attemptGetStoreInformation(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is GetCustomCategoriesEvent ->{
                        accountRepository.attemptGetCustomCategories(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is GetProductsEvent ->{
                         accountRepository.attemptGetProducts(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is CreateOfferEvent ->{
                        stateEvent.offer.providerId=authToken.account_pk!!.toLong()
                        accountRepository.attemptCreateOffer(
                            stateEvent,
                            stateEvent.offer
                        )
                    }

                    is GetOffersEvent ->{
                        accountRepository.attemptGetOffers(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            getSelectedOfferFilter()?.second
                        )
                    }

                    is DeleteOfferEvent ->{
                        accountRepository.attemptDeleteOffer(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is UpdateOfferEvent ->{
                        stateEvent.offer.providerId=authToken.account_pk!!.toLong()
                        accountRepository.attemptUpdateOffer(
                            stateEvent,
                            stateEvent.offer
                        )
                    }

                    is AllCategoriesEvent ->{
                        accountRepository.attemptAllCategory(
                            stateEvent
                        )
                    }

                    is CreateFlashDealEvent ->{
                        stateEvent.flashDeal.providerId=authToken.account_pk!!.toLong()
                        accountRepository.attemptCreateFlashDeal(
                            stateEvent,
                            stateEvent.flashDeal
                        )
                    }

                    is GetFlashDealsEvent ->{
                        accountRepository.attemptGetFlashDeals(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is GetSearchFlashDealsEvent ->{
                        accountRepository.attemptGetSearchFlashDeals(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            getSearchFlashDealRangeDate().first,
                            getSearchFlashDealRangeDate().second
                        )
                    }

                    else -> {
                        flow{
                            emit(
                                DataState.error<AccountViewState>(
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

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }
}