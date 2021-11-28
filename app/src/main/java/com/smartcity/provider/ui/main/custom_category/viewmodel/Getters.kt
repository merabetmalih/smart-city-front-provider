package com.smartcity.provider.ui.main.custom_category.viewmodel

import android.net.Uri
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Attribute
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState

fun CustomCategoryViewModel.getCustomCategoryFields():List<CustomCategory>{
    getCurrentViewStateOrNew().let {
        return it.customCategoryFields.customCategoryList?: arrayListOf()
    }
}

fun CustomCategoryViewModel.getProductName(): String {
    getCurrentViewStateOrNew().let {
        return it.productFields.name?:""
    }
}

fun CustomCategoryViewModel.getProductDescription(): String {
    getCurrentViewStateOrNew().let {
        return it.productFields.description?:""
    }
}
fun CustomCategoryViewModel.getProductPrice(): String {
    getCurrentViewStateOrNew().let {
        return it.productFields.price?:""
    }
}
fun CustomCategoryViewModel.getProductQuantity(): String {
    getCurrentViewStateOrNew().let {
        return it.productFields.quantity?:""
    }
}

fun CustomCategoryViewModel.getSelectedCustomCategory():CustomCategory?{
    getCurrentViewStateOrNew().let {
        return it.selectedCustomCategory.customCategory
    }
}

fun CustomCategoryViewModel.getNewOption(): Attribute?{
    getCurrentViewStateOrNew().let {
        return it.newOption.attribute
    }
}

fun CustomCategoryViewModel.getOptionList():HashSet <Attribute>{
    getCurrentViewStateOrNew().let {
        return it.productFields.attributeList
    }
}

fun CustomCategoryViewModel.getProductVariantsList():List<ProductVariants>{
    getCurrentViewStateOrNew().let {
        return it.productFields.productVariantList?: arrayListOf()
    }
}

fun CustomCategoryViewModel.getCopyImage(): Uri?{
    getCurrentViewStateOrNew().let {
        return it.copyImage.copyImage
    }
}

fun CustomCategoryViewModel.getProductList():List<Product>{
    getCurrentViewStateOrNew().let {
        return it.productList.products?: arrayListOf()
    }
}

fun CustomCategoryViewModel.isEmptyProductFields():Boolean{
    val update = getCurrentViewStateOrNew()
    if (update.productFields.name== null){
        return true
    }
    return false
}

fun CustomCategoryViewModel.getProductImageList():List<Uri>{
    getCurrentViewStateOrNew().let {
        return it.productFields.productImageList?: listOf()
    }
}

fun CustomCategoryViewModel.getSelectedProductVariant(): ProductVariants?{
    getCurrentViewStateOrNew().let {
        return it.selectedProductVariant.variante
    }
}

fun CustomCategoryViewModel.getViewProductFields(): Product?{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.product
    }
}

fun CustomCategoryViewModel.getChoisesMap():MutableMap<String, String>{
    getCurrentViewStateOrNew().let {
        return it.choisesMap.choises?: mutableMapOf()
    }
}