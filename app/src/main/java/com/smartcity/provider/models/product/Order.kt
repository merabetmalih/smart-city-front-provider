package com.smartcity.provider.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    @SerializedName("id")
    @Expose
    var id:Long,

    @SerializedName("orderProductVariants")
    @Expose
    var orderProductVariants: List<OrderProductVariant>,

    @SerializedName("bill")
    @Expose
    var bill:Bill,

    @SerializedName("orderType")
    @Expose
    var orderType:OrderType,

    @SerializedName("address")
    @Expose
    var address:Address?,

    @SerializedName("receiverFirstName")
    @Expose
    var firstName:String?,

    @SerializedName("receiverLastName")
    @Expose
    var lastName:String?,

    @SerializedName("receiverBirthDay")
    @Expose
    var birthDay:String?,

    @SerializedName("createAt")
    @Expose
    var createAt:String,

    @SerializedName("validDuration")
    @Expose
    var validDuration:Long,

    @SerializedName("orderState")
    @Expose
    var orderState:OrderState,

    @SerializedName("providerNote")
    @Expose
    var providerNote:String?,

) : Parcelable {
    override fun toString(): String {
        return "Order(id=$id," +
                "bill=$bill," +
                "orderType=$orderType" +
                "address=$address" +
                "firstName=$firstName," +
                "lastName=$lastName," +
                "birthDay=$birthDay," +
                "createAt=$createAt," +
                "validDuration=$validDuration," +
                "orderState=$orderState," +
                "providerNote=$providerNote," +
                ")"
    }
}