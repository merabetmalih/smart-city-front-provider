package com.smartcity.provider.ui.main.account.viewmodel

import com.smartcity.provider.models.*
import com.smartcity.provider.models.product.Product

fun AccountViewModel.getPolicyConfigurationDelivery(): Boolean? {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.delivery
    }
}

fun AccountViewModel.getPolicyConfigurationSelfPickUpOption(): SelfPickUpOptions? {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.selfPickUpOption
    }
}

fun AccountViewModel.getPolicyConfigurationValidDuration(): Long? {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.validDuration
    }
}

fun AccountViewModel.getPolicyConfigurationTax(): Int? {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.tax
    }
}

fun AccountViewModel.getPolicyConfigurationTaxRanges():List<TaxRange> {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.taxRanges?: listOf()
    }
}

fun AccountViewModel.getCustomCategoryList():List<CustomCategory>{
    getCurrentViewStateOrNew().let {
        return it.discountFields.customCategoryList?: listOf()
    }
}

fun AccountViewModel.getSelectedCustomCategory():CustomCategory?{
    getCurrentViewStateOrNew().let {
        return it.discountFields.selectedCustomCategory
    }
}

fun AccountViewModel.getProductList():List<Product>{
    getCurrentViewStateOrNew().let {
        return it.discountFields.productsList?: arrayListOf()
    }
}

fun AccountViewModel.getSelectedProductDiscount():List<Product>{
    getCurrentViewStateOrNew().let {
        return it.discountFields.selectedProductDiscount?: listOf()
    }
}

fun AccountViewModel.getSelectedProductToSelectVariant():Product?{
    getCurrentViewStateOrNew().let {
        return it.discountFields.selectedProductToSelectVariant
    }
}

fun AccountViewModel.getRangeDiscountDate():Pair<String?,String?>{
    getCurrentViewStateOrNew().let {
        return it.discountFields.rangeDiscountDate
    }
}

fun AccountViewModel.getDiscountCode():String{
    getCurrentViewStateOrNew().let {
        return it.discountFields.discountCode?:""
    }
}

fun AccountViewModel.getOfferType(): OfferType {
    getCurrentViewStateOrNew().let {
        return it.discountFields.offerType?:OfferType.PERCENTAGE
    }
}

fun AccountViewModel.getDiscountValuePercentage():String{
    getCurrentViewStateOrNew().let {
        return it.discountFields.discountValuePercentage?:"%"
    }
}

fun AccountViewModel.getDiscountValueFixed():String{
    getCurrentViewStateOrNew().let {
        return it.discountFields.discountValueFixed?:""
    }
}

fun AccountViewModel.getOffersList():List<Offer>{
    getCurrentViewStateOrNew().let {
        return it.discountOfferList.offersList?: listOf()
    }
}

fun AccountViewModel.getSelectedOffer():Offer?{
    getCurrentViewStateOrNew().let {
        return it.discountOfferList.selectedOffer
    }
}

fun AccountViewModel.getSelectedCategoriesList():MutableList<Category>{
    getCurrentViewStateOrNew().let {
        return it.storeInformationFields.selectedCategories
    }
}

fun AccountViewModel.getCategoriesList():List<Category>{
    getCurrentViewStateOrNew().let {
        return it.storeInformationFields.categoryList?: listOf()
    }
}

fun AccountViewModel.getStoreInformation():StoreInformation?{
    getCurrentViewStateOrNew().let {
        return it.storeInformationFields.storeInformation
    }
}

fun AccountViewModel.getSelectedCategory(): Category?{
    getCurrentViewStateOrNew().let {
        return it.storeInformationFields.selectedCategory
    }
}

fun AccountViewModel.getFlashDealsList(): List<FlashDeal>{
    getCurrentViewStateOrNew().let {
        return it.flashDealsFields.flashDealsList?: listOf()
    }
}

fun AccountViewModel.getSearchFlashDealRangeDate(): Pair<String?,String?>{
    getCurrentViewStateOrNew().let {
        return it.flashDealsFields.rangeDate
    }
}

fun AccountViewModel.getSearchFlashDealsList(): List<FlashDeal>{
    getCurrentViewStateOrNew().let {
        return it.flashDealsFields.searchFlashDealsList?: listOf()
    }
}

fun AccountViewModel.getSelectedOfferFilter(): Pair<String,OfferState>?{
    getCurrentViewStateOrNew().let {
        return it.discountFields.selectedOfferFilter
    }
}

fun AccountViewModel.getNotificationSettings(): List<String>{
    getCurrentViewStateOrNew().let {
        return it.notificationSettings?: listOf()
    }
}