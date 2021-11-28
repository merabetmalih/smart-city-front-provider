package com.smartcity.provider.ui.main.store.state

import android.os.Parcelable
import com.smartcity.provider.models.AccountProperties
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import kotlinx.android.parcel.Parcelize

const val STORE_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.StoreViewState"

@Parcelize
class StoreViewState(

    var viewCustomCategoryFields: ViewCustomCategoryFields = ViewCustomCategoryFields(),

    var viewProductList: ViewProductList = ViewProductList(),

    var viewProductFields: ViewProductFields = ViewProductFields(),

    var choisesMap: ChoisesMap = ChoisesMap(),

    var customCategoryRecyclerPosition:Int?=null

    ) : Parcelable{

    @Parcelize
    data class ViewProductList(
        var products:List<Product>? = null
    ) : Parcelable

    @Parcelize
    data class ViewCustomCategoryFields(
        var customCategoryList: List<CustomCategory>? = null
    ) : Parcelable

    @Parcelize
    data class ViewProductFields(
        var product: Product? = null
    ) : Parcelable

    @Parcelize
    data class ChoisesMap(
        var choises:MutableMap<String, String>? = null
    ) : Parcelable
}