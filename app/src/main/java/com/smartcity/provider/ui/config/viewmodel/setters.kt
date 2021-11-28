package com.smartcity.provider.ui.config.viewmodel

import android.net.Uri
import com.smartcity.provider.models.Category
import com.smartcity.provider.models.StoreAddress

fun ConfigViewModel.setListAllCategoryStore(list: List<Category>){
    val update = getCurrentViewStateOrNew()
    update.storeFields.allCategoryStore = list
    setViewState(update)
}

fun ConfigViewModel.setStoreCategories(list :List<Category>?){
    val update = getCurrentViewStateOrNew()
    update.storeFields.storeCategory = list
    setViewState(update)
}

fun ConfigViewModel.setStoreName(value:String){
    val update = getCurrentViewStateOrNew()
    update.storeFields.storeName = value
    setViewState(update)
}

fun ConfigViewModel.setStoreDescription(value:String){
    val update = getCurrentViewStateOrNew()
    update.storeFields.storeDescription = value
    setViewState(update)
}

fun ConfigViewModel.setStoreAddress(value: StoreAddress){
    val update = getCurrentViewStateOrNew()
    update.storeFields.storeAddress = value
    setViewState(update)
}

fun ConfigViewModel.setSelectedStoreCategories(list: MutableList<Category>){
    val update = getCurrentViewStateOrNew()
    update.storeFields.selectedStoreCategory = list
    setViewState(update)
}

fun ConfigViewModel.setSelectedCategory(value : Category){
    val update = getCurrentViewStateOrNew()
    update.storeFields.selectedCategory = value
    setViewState(update)
}

fun ConfigViewModel.setNewImageUri(value: Uri?){
    val update = getCurrentViewStateOrNew()
    update.storeFields.newImageUri = value
    setViewState(update)
}