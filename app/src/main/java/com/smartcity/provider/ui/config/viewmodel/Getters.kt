package com.smartcity.provider.ui.config.viewmodel

import android.net.Uri
import com.smartcity.provider.models.Category
import com.smartcity.provider.models.StoreAddress

fun ConfigViewModel.getAllCategoryStore():List<Category>?{
    getCurrentViewStateOrNew().let {
        return it.storeFields.allCategoryStore
    }
}

fun ConfigViewModel.getStoreCategories():List<Category>?{
    getCurrentViewStateOrNew().let {
        return it.storeFields.storeCategory
    }
}

fun ConfigViewModel.getStoreName():String{
    getCurrentViewStateOrNew().let {
        return it.storeFields.storeName?:""
    }
}

fun ConfigViewModel.getStoreDescription():String{
    getCurrentViewStateOrNew().let {
        return it.storeFields.storeDescription?:""
    }
}

fun ConfigViewModel.getStoreAddress(): StoreAddress?{
    getCurrentViewStateOrNew().let {
        return it.storeFields.storeAddress
    }
}

fun ConfigViewModel.getSelectedStoreCategories(): MutableList<Category>{
    getCurrentViewStateOrNew().let {
        return it.storeFields.selectedStoreCategory?: mutableListOf()
    }
}

fun ConfigViewModel.getSelectedCategory(): Category?{
    getCurrentViewStateOrNew().let {
        return it.storeFields.selectedCategory
    }
}

fun ConfigViewModel.getNewImageUri(): Uri?{
    getCurrentViewStateOrNew().let {
        return it.storeFields.newImageUri
    }
}