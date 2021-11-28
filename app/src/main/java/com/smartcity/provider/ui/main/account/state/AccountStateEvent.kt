package com.smartcity.provider.ui.main.account.state

import com.smartcity.provider.models.FlashDeal
import com.smartcity.provider.models.Offer
import com.smartcity.provider.models.Policy
import com.smartcity.provider.models.StoreInformation
import com.smartcity.provider.util.StateEvent

sealed class AccountStateEvent: StateEvent {

    class SaveNotificationSettingsEvent(
        val settings:List<String>
    ):AccountStateEvent() {
        override fun errorInfo(): String {
            return "Save notification sitings attempt failed."
        }

        override fun toString(): String {
            return "SaveNotificationSettingsStateEvent"
        }
    }

    class GetNotificationSettingsEvent():AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get notification settings attempt failed."
        }

        override fun toString(): String {
            return "GetNotificationSettingsStateEvent"
        }
    }

    class SavePolicyEvent(
        var policy: Policy
    ):AccountStateEvent(){
        override fun errorInfo(): String {
            return "Save policy attempt failed."
        }

        override fun toString(): String {
            return "SavePolicyStateEvent"
        }
    }

    class SetStoreInformationEvent(
        var storeInformation: StoreInformation
    ):AccountStateEvent(){
        override fun errorInfo(): String {
            return "Save store information attempt failed."
        }

        override fun toString(): String {
            return "SetStoreInformationStateEvent"
        }
    }

    class GetStoreInformationEvent(
    ):AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get store information attempt failed."
        }

        override fun toString(): String {
            return "GetStoreInformationStateEvent"
        }
    }

    class AllCategoriesEvent:AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get all categories attempt failed."
        }

        override fun toString(): String {
            return "AllCategoriesStateEvent"
        }
    }

    class GetCustomCategoriesEvent : AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get categories attempt failed."
        }

        override fun toString(): String {
            return "GetCustomCategoriesStateEvent"
        }
    }

    class GetProductsEvent(
        val id: Long
    ) : AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get products attempt failed."
        }

        override fun toString(): String {
            return "GetProductsStateEvent"
        }
    }

    class CreateOfferEvent(
        val offer: Offer
    ) : AccountStateEvent(){
        override fun errorInfo(): String {
            return "Offer creation attempt failed."
        }

        override fun toString(): String {
            return "CreateOfferStateEvent"
        }
    }

    class GetOffersEvent():AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get offers attempt failed."
        }

        override fun toString(): String {
            return "GetOffersStateEvent"
        }
    }

    class DeleteOfferEvent(
        val id:Long
    ):AccountStateEvent(){
        override fun errorInfo(): String {
            return "Delete offer attempt failed."
        }

        override fun toString(): String {
            return "DeleteOfferStateEvent"
        }
    }

    class UpdateOfferEvent(
        val offer: Offer
    ) : AccountStateEvent(){
        override fun errorInfo(): String {
            return "Update offer attempt failed."
        }

        override fun toString(): String {
            return "UpdateOfferStateEvent"
        }
    }

    class CreateFlashDealEvent(
        val flashDeal: FlashDeal
    ): AccountStateEvent(){
        override fun errorInfo(): String {
            return "Create flash offer attempt failed."
        }

        override fun toString(): String {
            return "CreateFlashDealStateEvent"
        }
    }

    class GetFlashDealsEvent():AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get flash offers attempt failed."
        }

        override fun toString(): String {
            return "GetFlashDealsStateEvent"
        }
    }

    class GetSearchFlashDealsEvent():AccountStateEvent(){
        override fun errorInfo(): String {
            return "Search flash offers attempt failed."
        }

        override fun toString(): String {
            return "GetSearchFlashDealsStateEvent"
        }
    }

    class None: AccountStateEvent(){
        override fun errorInfo(): String {
            return "None"
        }
    }
}