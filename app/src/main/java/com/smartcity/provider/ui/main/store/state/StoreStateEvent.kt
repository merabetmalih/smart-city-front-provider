package com.smartcity.provider.ui.main.store.state

import com.smartcity.provider.util.StateEvent

sealed class StoreStateEvent: StateEvent{

    class CustomCategoryMainEvent : StoreStateEvent() {
        override fun errorInfo(): String {
            return "Get categories attempt failed."
        }

        override fun toString(): String {
            return "CustomCategoryMainStateEvent"
        }
    }

    class ProductMainEvent(
        val id: Long
    ) : StoreStateEvent(){
        override fun errorInfo(): String {
            return "Get products attempt failed."
        }

        override fun toString(): String {
            return "ProductMainStateEvent"
        }
    }

    class AllProductEvent() : StoreStateEvent(){
        override fun errorInfo(): String {
            return "Get all products attempt failed."
        }

        override fun toString(): String {
            return "AllProductStateEvent"
        }
    }

    class None: StoreStateEvent(){
        override fun errorInfo(): String {
            return "None"
        }
    }
}