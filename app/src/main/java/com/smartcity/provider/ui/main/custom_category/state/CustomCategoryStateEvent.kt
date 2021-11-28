package com.smartcity.provider.ui.main.custom_category.state

import com.smartcity.provider.models.product.Product
import com.smartcity.provider.util.StateEvent
import okhttp3.MultipartBody
import okhttp3.RequestBody

sealed class CustomCategoryStateEvent: StateEvent {

    class CustomCategoryMainEvent : CustomCategoryStateEvent() {
        override fun errorInfo(): String {
            return "Get categories attempt failed."
        }

        override fun toString(): String {
            return "CustomCategoryMainStateEvent"
        }
    }

    data class CreateCustomCategoryEvent(
        val name:String
    ):CustomCategoryStateEvent(){
        override fun errorInfo(): String {
            return "Create category attempt failed."
        }

        override fun toString(): String {
            return "CreateCustomCategoryStateEvent"
        }
    }

    data class DeleteCustomCategoryEvent(
        val id:Long
    ):CustomCategoryStateEvent(){
        override fun errorInfo(): String {
            return "Delete category attempt failed."
        }

        override fun toString(): String {
            return "DeleteCustomCategoryStateEvent"
        }
    }

    data class UpdateCustomCategoryEvent(
        val id:Long,
        val name: String,
        val provider:Long
    ):CustomCategoryStateEvent(){
        override fun errorInfo(): String {
            return "Update category attempt failed."
        }

        override fun toString(): String {
            return "UpdateCustomCategoryStateEvent"
        }
    }

    data class CreateProductEvent(
        val product: RequestBody,
        val productImagesFile: List<MultipartBody.Part>,
        val variantesImagesFile : List<MultipartBody.Part>,
        val productObject: Product
    ):CustomCategoryStateEvent(){
        override fun errorInfo(): String {
            return "Create product attempt failed."
        }

        override fun toString(): String {
            return "CreateProductStateEvent"
        }
    }

    data class UpdateProductEvent(
        val product: RequestBody,
        val productImagesFile: List<MultipartBody.Part>,
        val variantesImagesFile : List<MultipartBody.Part>,
        val productObject: Product
    ):CustomCategoryStateEvent(){
        override fun errorInfo(): String {
            return "Update product attempt failed."
        }

        override fun toString(): String {
            return "UpdateProductStateEvent"
        }
    }

    class ProductMainEvent(
        val id: Long
    ) : CustomCategoryStateEvent(){
        override fun errorInfo(): String {
            return "Get products attempt failed."
        }

        override fun toString(): String {
            return "ProductMainStateEvent"
        }
    }

    data class DeleteProductEvent(
        val id:Long
    ):CustomCategoryStateEvent(){
        override fun errorInfo(): String {
            return "Delete product attempt failed."
        }

        override fun toString(): String {
            return "DeleteProductStateEvent"
        }
    }

    data class UpdateProductsCustomCategoryEvent(
        val products: List<Long>,
        val category: Long
    ): CustomCategoryStateEvent(){
        override fun errorInfo(): String {
            return "Update product category attempt failed."
        }

        override fun toString(): String {
            return "UpdateProductsCustomCategoryStateEvent"
        }
    }

    class None: CustomCategoryStateEvent(){
        override fun errorInfo(): String {
            return "None"
        }
    }
}