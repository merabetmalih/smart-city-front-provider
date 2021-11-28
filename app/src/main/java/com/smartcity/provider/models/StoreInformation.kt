package com.smartcity.provider.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreInformation(
    @SerializedName("providerId")
    @Expose
    var providerId:Long?,
    @SerializedName("address")
    @Expose
    var address:String,

    @SerializedName("telephoneNumber")
    @Expose
    var telephoneNumber:String,

    @SerializedName("defaultTelephoneNumber")
    @Expose
    var defaultTelephoneNumber:String,

    @SerializedName("defaultCategories")
    @Expose
    var defaultCategories:List<String>?,

    @SerializedName("defaultCategoriesList")
    @Expose
    var defaultCategoriesList:List<Category>?
) : Parcelable {
}