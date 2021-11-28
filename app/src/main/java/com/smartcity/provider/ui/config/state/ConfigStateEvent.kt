package com.smartcity.provider.ui.config.state

import com.smartcity.provider.models.Store
import com.smartcity.provider.util.StateEvent
import okhttp3.MultipartBody

sealed class ConfigStateEvent: StateEvent {

    data class CreateStoreAttemptEvent(
        val store: Store,
        val image: MultipartBody.Part
    ): ConfigStateEvent() {
        override fun errorInfo(): String {
            return "Store creation attempt failed."
        }

        override fun toString(): String {
            return "CreateStoreAttemptStateEvent"
        }
    }

    class GetStoreCategoriesEvent(): ConfigStateEvent() {
        override fun errorInfo(): String {
            return "Get store categories attempt failed."
        }

        override fun toString(): String {
            return "GetStoreCategoriesStateEvent"
        }
    }

    class SetStoreCategoriesEvent(
        var categories : List<String>
    ): ConfigStateEvent() {
        override fun errorInfo(): String {
            return "Set store categories attempt failed."
        }

        override fun toString(): String {
            return "SetStoreCategoriesStateEvent"
        }
    }

    class GetAllCategoriesEvent(): ConfigStateEvent() {
        override fun errorInfo(): String {
            return "Get all categories attempt failed."
        }

        override fun toString(): String {
            return "GetAllCategoriesStateEvent"
        }
    }

    class None: ConfigStateEvent() {
        override fun errorInfo(): String {
            return "None"
        }
    }
}