package com.smartcity.provider.ui.main.custom_category.viewmodel

import android.net.Uri
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Attribute
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState

fun CustomCategoryViewModel.setCustomCategoryFields(customCategoryFields: CustomCategoryViewState.CustomCategoryFields){
    val update = getCurrentViewStateOrNew()
    if(update.customCategoryFields == customCategoryFields){
        return
    }
    update.customCategoryFields = customCategoryFields
    setViewState(update)
}

fun CustomCategoryViewModel.setSelectedCustomCategory(selectedCustomCategory: CustomCategory){
    val update = getCurrentViewStateOrNew()
    update.selectedCustomCategory.customCategory = selectedCustomCategory
    setViewState(update)
}

fun CustomCategoryViewModel.setProductFields(productFields: CustomCategoryViewState.ProductFields){
    val update = getCurrentViewStateOrNew()
    update.productFields = productFields
    setViewState(update)
}

fun CustomCategoryViewModel.setNewOption(attribute: Attribute?){
    val update = getCurrentViewStateOrNew()
    update.newOption.attribute = attribute
    setViewState(update)
}

fun CustomCategoryViewModel.setOptionList(attribute: Attribute){
    val update = getCurrentViewStateOrNew()
    val optionList= update.productFields.attributeList
    optionList.find { it.name.equals(attribute.name) }.let {
        if (it != null) {
            it.attributeValues=attribute.attributeValues
        }else{
            optionList.add(attribute)
        }
    }
    update.productFields.attributeList=optionList
    setViewState(update)
}

fun CustomCategoryViewModel.setOptionList(attributeSet: HashSet <Attribute>){
    val update = getCurrentViewStateOrNew()
    update.productFields.attributeList=attributeSet
    setViewState(update)
}

fun CustomCategoryViewModel.setProductVariantsList(productVariants: MutableList<ProductVariants>){
    val update = getCurrentViewStateOrNew()
    update.productFields.productVariantList=productVariants
    setViewState(update)
}

fun CustomCategoryViewModel.setCopyImage(uri: Uri?){
    val update = getCurrentViewStateOrNew()
    update.copyImage.copyImage=uri
    setViewState(update)
}

fun CustomCategoryViewModel.setProductList(productList: CustomCategoryViewState.ProductList){
    val update = getCurrentViewStateOrNew()
    update.productList=productList
    setViewState(update)
}

fun CustomCategoryViewModel.setProductImageList(image: Uri){
    val update = getCurrentViewStateOrNew()
    if (update.productFields.productImageList.isNullOrEmpty()){
        update.productFields.productImageList = mutableListOf()
        setViewState(update)
    }
    update.productFields.productImageList?.add(image)
    setViewState(update)
}

fun CustomCategoryViewModel.setCustomCategoryList(list: List<CustomCategory>){
    val update = getCurrentViewStateOrNew()
    update.customCategoryFields.customCategoryList=list
    setViewState(update)
}

fun CustomCategoryViewModel.setProducts(list: List<Product>){
    val update = getCurrentViewStateOrNew()
    update.productList.products=list
    setViewState(update)
}

fun CustomCategoryViewModel.setProductImageList(images: MutableList<Uri>){
    val update = getCurrentViewStateOrNew()
    update.productFields.productImageList=images
    setViewState(update)
}

fun CustomCategoryViewModel.setSelectedProductVariant(productVariants: ProductVariants?){
    val update = getCurrentViewStateOrNew()
    update.selectedProductVariant.variante = productVariants
    setViewState(update)
}

fun CustomCategoryViewModel.setViewProductFields(product: Product){
    val update = getCurrentViewStateOrNew()
    update.viewProductFields.product = product
    setViewState(update)
}

fun CustomCategoryViewModel.setChoisesMap(map: MutableMap<String, String>){
    val update = getCurrentViewStateOrNew()
    update.choisesMap.choises = map
    setViewState(update)
}

fun CustomCategoryViewModel.clearChoisesMap(){
    val update = getCurrentViewStateOrNew()
    update.choisesMap= CustomCategoryViewState.ChoisesMap()
    setViewState(update)
}

fun CustomCategoryViewModel.clearProductFields(){
    val update = getCurrentViewStateOrNew()
    update.newOption= CustomCategoryViewState.NewOption()
    /* update.productFields.description=""
     update.productFields.name=""
     update.productFields.price=""
     update.productFields.quantity=""*/
    update.productFields= CustomCategoryViewState.ProductFields()
    update.selectedProductVariant= CustomCategoryViewState.SelectedProductVariant()
    setViewState(update)
}

fun CustomCategoryViewModel.clearViewProductFields(){
    val update = getCurrentViewStateOrNew()
    update.viewProductFields= CustomCategoryViewState.ViewProductFields()
    setViewState(update)
}

fun CustomCategoryViewModel.clearProductList(){
    val update = getCurrentViewStateOrNew()
    update.productList= CustomCategoryViewState.ProductList()
}