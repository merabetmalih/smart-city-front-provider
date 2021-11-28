package com.smartcity.provider.ui.main.custom_category.state

import android.net.Uri
import android.os.Parcelable
import android.util.Log
import com.smartcity.provider.models.BlogPost
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Attribute
import com.smartcity.provider.models.product.AttributeValue
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants
import com.smartcity.provider.ui.auth.state.LoginFields
import kotlinx.android.parcel.Parcelize

const val CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY = "com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState"

@Parcelize
class CustomCategoryViewState(
    var customCategoryFields:CustomCategoryFields=CustomCategoryFields(),
    var selectedCustomCategory:SelectedCustomCategory=SelectedCustomCategory(),

    var newOption:NewOption=NewOption(),

    var selectedProductVariant:SelectedProductVariant=SelectedProductVariant(),

    var productFields:ProductFields=ProductFields(),

    var productList:ProductList=ProductList(),

    var viewProductFields:ViewProductFields=ViewProductFields(),

    var choisesMap:ChoisesMap=ChoisesMap(),

    var copyImage:CopyImage=CopyImage()

) : Parcelable {
    @Parcelize
    data class CustomCategoryFields(
        var customCategoryList: List<CustomCategory>? = null,
        var layoutManagerState: Parcelable? = null
    ) : Parcelable

    @Parcelize
    data class SelectedCustomCategory(
        var customCategory: CustomCategory? =null
    ) : Parcelable

    @Parcelize
    data class NewOption(
        var attribute: Attribute? =null
    ) : Parcelable


    @Parcelize
    data class SelectedProductVariant(
        var variante:ProductVariants ? =null
    ) : Parcelable

    @Parcelize
    data class ProductList(
        var products:List<Product> ? = null
    ) : Parcelable

    @Parcelize
    data class ViewProductFields(
        var product: Product? = null
    ) : Parcelable

    @Parcelize
    data class CopyImage(
        var copyImage :Uri?=null
    ) : Parcelable

    @Parcelize
    data class ProductFields(
        var description: String ? = null,
        var name: String ? = null,
        var price: String ? = null,
        var quantity:String ? = null,
        var productImageList: MutableList<Uri> ? = null,
        var productVariantList: MutableList<ProductVariants> ? = null,
        var attributeList: HashSet <Attribute> = LinkedHashSet ()
    ) : Parcelable{
        class CreateProductError {

            companion object{

                fun mustFillAllFields(): String{
                    return "You can't create product without fill all information."
                }

                fun none():String{
                    return "None"
                }

            }
        }


        fun isValidForCreation(): String{

            if(description.isNullOrEmpty()
                || name.isNullOrEmpty()
                || productVariantList.isNullOrEmpty()
                || productVariantList?.map { it.price==0.0 }?.all{!it}?.not() != false
                || productVariantList?.map { it.unit==0 }?.all{!it}?.not() != false
            ){

                return CreateProductError.mustFillAllFields()
            }
            return CreateProductError.none()
        }

    }

    @Parcelize
    data class ChoisesMap(
        var choises:MutableMap<String, String> ? = null
    ) : Parcelable
}