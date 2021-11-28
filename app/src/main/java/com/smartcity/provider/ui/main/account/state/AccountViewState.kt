package com.smartcity.provider.ui.main.account.state

import android.os.Parcelable
import com.smartcity.provider.models.*
import com.smartcity.provider.models.product.Product
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"
@Parcelize
class AccountViewState(
    var notificationSettings:List<String>? = null,
    var policyConfiguration:PolicyConfiguration=PolicyConfiguration(),
    var storeInformationFields: StoreInformationFields=StoreInformationFields(),
    var discountFields: DiscountFields =DiscountFields(),
    var discountOfferList :DiscountOfferList = DiscountOfferList(),
    var flashDealsFields:FlashDealsFields = FlashDealsFields()
) : Parcelable {

    @Parcelize
    data class FlashDealsFields(
        var flashDealsList:List<FlashDeal>? = null,
        var searchFlashDealsList:List<FlashDeal>? = null,
        var rangeDate:Pair<String?,String?> =Pair(null,null)
    ) : Parcelable

    @Parcelize
    data class StoreInformationFields(
        var storeInformation: StoreInformation?=null,
        var categoryList:List<Category>? = null,
        var selectedCategory:Category? = null,

        var selectedCategories:MutableList<Category> = mutableListOf()
    ) : Parcelable

    @Parcelize
    data class PolicyConfiguration(
        var delivery:Boolean?=null,
        var selfPickUpOption: SelfPickUpOptions?=null,
        var validDuration:Long?=null,
        var tax:Int?=null,
        var taxRanges:List<TaxRange>? = null
    ) : Parcelable

    @Parcelize
    data class DiscountOfferList(
        var offersList:List<Offer>? = null,
        var selectedOffer:Offer?=null
    ) : Parcelable

    @Parcelize
    data class DiscountFields(
        var customCategoryList: List<CustomCategory>? = null,
        var selectedCustomCategory:CustomCategory?=null,
        var productsList:List<Product>? = null,
        var selectedProductToSelectVariant:Product?=null,

        var selectedProductDiscount:List<Product>? = null,
        var rangeDiscountDate:Pair<String?,String?> =Pair(null,null),
        var discountCode:String? = null,
        var offerType:OfferType? = null,
        var discountValuePercentage:String? = null,
        var discountValueFixed:String? = null,

        var selectedOfferFilter: Pair<String,OfferState>? = null
    ) : Parcelable{
        class CreateOfferError {

            companion object{

                fun mustFillAllFields(): String{
                    return "You can't create offer without fill all information."
                }

                fun none():String{
                    return "None"
                }

            }
        }

        fun isValidForCreation(): String{
            if(selectedProductDiscount.isNullOrEmpty()
                || rangeDiscountDate.first==null
                || rangeDiscountDate.second==null
                || discountCode.isNullOrBlank()
                || discountCode.isNullOrEmpty()){
                return CreateOfferError.mustFillAllFields()
            }


            if(offerType==OfferType.FIXED && (discountValueFixed.isNullOrBlank() || discountValueFixed.isNullOrEmpty())){
                return CreateOfferError.mustFillAllFields()
            }

            if (offerType==OfferType.PERCENTAGE && discountValuePercentage=="%"){
                return CreateOfferError.mustFillAllFields()
            }

            return CreateOfferError.none()
        }
    }
}