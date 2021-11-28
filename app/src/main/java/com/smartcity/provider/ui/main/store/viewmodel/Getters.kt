package com.smartcity.provider.ui.main.store.viewmodel

import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.main.store.state.StoreViewState

fun StoreViewModel.getViewProductList(): StoreViewState.ViewProductList {
    getCurrentViewStateOrNew().let {
        return it.viewProductList
    }
}

fun StoreViewModel.getViewCustomCategoryFields():List<CustomCategory>{
    getCurrentViewStateOrNew().let {
        return it.viewCustomCategoryFields.customCategoryList?: arrayListOf()
    }
}

fun StoreViewModel.getViewProductFields(): Product?{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.product
    }
}

fun StoreViewModel.getChoisesMap():MutableMap<String, String>{
    getCurrentViewStateOrNew().let {
        return it.choisesMap.choises?: mutableMapOf()
    }
}

fun StoreViewModel.getCustomCategoryRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.customCategoryRecyclerPosition?:0
    }
}