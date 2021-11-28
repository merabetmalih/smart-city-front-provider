package com.smartcity.provider.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.provider.models.product.Address
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Offer(
    @SerializedName("id")
    @Expose
    var id:Long?,

    @SerializedName("discountCode")
    @Expose
    var discountCode:String?,

    @SerializedName("type")
    @Expose
    var type:OfferType?,

    @SerializedName("newPrice")
    @Expose
    var newPrice:Double?,

    @SerializedName("percentage")
    @Expose
    var percentage:Int?,

    @SerializedName("startDate")
    @Expose
    var startDate:String?,

    @SerializedName("endDate")
    @Expose
    var endDate:String?,

    @SerializedName("providerId")
    @Expose
    var providerId:Long?,

    @SerializedName("productVariantsId")
    @Expose
    var productVariantsId:List<Long>?,

    @SerializedName("products")
    @Expose
    var products:List<Product>?,

    @SerializedName("offerState")
    @Expose
    var offerState:OfferState?
) : Parcelable {

    override fun toString(): String {
        return "Offer(id=$id," +
                "discountCode=$discountCode," +
                "type=$type," +
                "newPrice=$newPrice," +
                "percentage=$percentage," +
                "startDate=$startDate," +
                "endDate=$endDate," +
                "providerId=$providerId," +
                "productVariantsId=$productVariantsId," +
                "offerState=$offerState," +
                "products=$products)"
    }

}