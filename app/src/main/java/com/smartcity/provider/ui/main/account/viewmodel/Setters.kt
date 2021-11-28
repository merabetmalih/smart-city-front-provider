package com.smartcity.provider.ui.main.account.viewmodel

import com.smartcity.provider.models.*
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.main.account.state.AccountViewState

fun AccountViewModel.setPolicyConfigurationDelivery(bool:Boolean){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.delivery = bool
    setViewState(update)
}

fun AccountViewModel.setPolicyConfigurationSelfPickUpOption(selfPickUpOption: SelfPickUpOptions){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.selfPickUpOption = selfPickUpOption
    setViewState(update)
}

fun AccountViewModel.setPolicyConfigurationValidDuration(duration:Long){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.validDuration = duration
    setViewState(update)
}

fun AccountViewModel.setPolicyConfigurationTax(tax:Int){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.tax = tax
    setViewState(update)
}

fun AccountViewModel.setPolicyConfigurationTaxRanges(taxRanges:List<TaxRange>){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.taxRanges = taxRanges
    setViewState(update)
}

fun AccountViewModel.clearPolicyConfiguration(){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration= AccountViewState.PolicyConfiguration()
    setViewState(update)
}

fun AccountViewModel.setCustomCategoryList(list:List<CustomCategory>){
    val update = getCurrentViewStateOrNew()
    update.discountFields.customCategoryList = list
    setViewState(update)
}

fun AccountViewModel.setSelectedCustomCategory(customCategory: CustomCategory){
    val update = getCurrentViewStateOrNew()
    update.discountFields.selectedCustomCategory = customCategory
    setViewState(update)
}

fun AccountViewModel.setProductList(products: List<Product>){
    val update = getCurrentViewStateOrNew()
    update.discountFields.productsList = products
    setViewState(update)
}

fun AccountViewModel.clearProductList(){
    val update = getCurrentViewStateOrNew()
    update.discountFields.productsList = listOf()
    setViewState(update)
}

fun AccountViewModel.setSelectedProductDiscount(list:List<Product>){
    val update = getCurrentViewStateOrNew()
    update.discountFields.selectedProductDiscount = list
    setViewState(update)
}

fun AccountViewModel.setSelectedProductToSelectVariant(product: Product){
    val update = getCurrentViewStateOrNew()
    update.discountFields.selectedProductToSelectVariant = product
    setViewState(update)
}

fun AccountViewModel.clearSelectedProductToSelectVariant(){
    val update = getCurrentViewStateOrNew()
    update.discountFields.selectedProductToSelectVariant = null
    setViewState(update)
}

fun AccountViewModel.setStartRangeDiscountDate(date:String){
    val update = getCurrentViewStateOrNew()
    val second=update.discountFields.rangeDiscountDate.second
    update.discountFields.rangeDiscountDate= Pair(date,second)
    setViewState(update)
}

fun AccountViewModel.setEndRangeDiscountDate(date:String){
    val update = getCurrentViewStateOrNew()
    val first=update.discountFields.rangeDiscountDate.first
    update.discountFields.rangeDiscountDate=Pair(first,date)
    setViewState(update)
}

fun AccountViewModel.setDiscountCode(code:String){
    val update = getCurrentViewStateOrNew()
    update.discountFields.discountCode = code
    setViewState(update)
}

fun AccountViewModel.setOfferType(type: OfferType) {
    val update = getCurrentViewStateOrNew()
    update.discountFields.offerType = type
    setViewState(update)
}

fun AccountViewModel.setDiscountValuePercentage(value:String){
    val update = getCurrentViewStateOrNew()
    update.discountFields.discountValuePercentage = value
    setViewState(update)
}

fun AccountViewModel.setDiscountValueFixed(value:String){
    val update = getCurrentViewStateOrNew()
    update.discountFields.discountValueFixed = value
    setViewState(update)
}

fun AccountViewModel.clearDiscountFields(){
    val update = getCurrentViewStateOrNew()
    val filter = update.discountFields.selectedOfferFilter
    update.discountFields=AccountViewState.DiscountFields()
    update.discountFields.selectedOfferFilter = filter
    setViewState(update)
}

fun AccountViewModel.setOffersList(list:List<Offer>){
    val update = getCurrentViewStateOrNew()
    update.discountOfferList.offersList = list
    setViewState(update)
}

fun AccountViewModel.setSelectedOffer(offer:Offer?){
    val update = getCurrentViewStateOrNew()
    update.discountOfferList.selectedOffer = offer
    setViewState(update)
}

fun AccountViewModel.setSelectedCategoriesList(list: MutableList<Category>){
    val update = getCurrentViewStateOrNew()
    update.storeInformationFields.selectedCategories = list
    setViewState(update)
}

fun AccountViewModel.setCategoriesList(list: List<Category>){
    val update = getCurrentViewStateOrNew()
    update.storeInformationFields.categoryList = list
    setViewState(update)
}

fun AccountViewModel.setStoreInformation(storeInformation: StoreInformation){
    val update = getCurrentViewStateOrNew()
    update.storeInformationFields.storeInformation = storeInformation
    setViewState(update)
}

fun AccountViewModel.setSelectedCategory(category: Category){
    val update = getCurrentViewStateOrNew()
    update.storeInformationFields.selectedCategory = category
    setViewState(update)
}

fun AccountViewModel.setFlashDealsList(list: List<FlashDeal>){
    val update = getCurrentViewStateOrNew()
    update.flashDealsFields.flashDealsList = list
    setViewState(update)
}

fun AccountViewModel.setSearchFlashDealRangeDate(range: Pair<String?,String?>){
    val update = getCurrentViewStateOrNew()
    update.flashDealsFields.rangeDate = range
    setViewState(update)
}

fun AccountViewModel.setSearchFlashDealsList(list: List<FlashDeal>){
    val update = getCurrentViewStateOrNew()
    update.flashDealsFields.searchFlashDealsList = list
    setViewState(update)
}

fun AccountViewModel.setSelectedOfferFilter(value:  Pair<String,OfferState>?){
    val update = getCurrentViewStateOrNew()
    update.discountFields.selectedOfferFilter = value
    setViewState(update)
}

fun AccountViewModel.setNotificationSettings(list : List<String>){
    val update = getCurrentViewStateOrNew()
    update.notificationSettings = list
    setViewState(update)
}
