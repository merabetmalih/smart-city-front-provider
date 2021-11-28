package com.smartcity.provider.ui.config.state

import android.net.Uri
import android.os.Parcelable
import com.smartcity.provider.models.Category
import com.smartcity.provider.models.StoreAddress
import kotlinx.android.parcel.Parcelize

const val CONFIG_VIEW_STATE_BUNDLE_KEY = "com.smartcity.provider.ui.config.state.ConfigViewState"

@Parcelize
data class ConfigViewState(
    var storeFields: StoreFields = StoreFields()
) : Parcelable

@Parcelize
data class StoreFields(
    var storeCategory: List<Category> ?=null,
    var allCategoryStore: List<Category> ?=null,
    var storeName: String? = null,
    var storeDescription: String? = null,
    var storeAddress: StoreAddress? = null,
    var selectedStoreCategory: MutableList<Category>?=null,
    var selectedCategory:Category? = null,
    var newImageUri: Uri? = null
) : Parcelable{
    class StoreError {

        companion object{

            fun mustFillAllFields(): String{
                return "You can't create store without an name and description and address."
            }

            fun none():String{
                return "None"
            }

        }
    }
    fun isValidForCreation(): String{

        if(storeName.isNullOrEmpty()
            || storeDescription.isNullOrEmpty()){

            return StoreError.mustFillAllFields()
        }
        return StoreError.none()
    }

    override fun toString(): String {
        return "StoreState(store_name=$storeName, store_description=$storeDescription, store_address=$storeAddress)"
    }
}