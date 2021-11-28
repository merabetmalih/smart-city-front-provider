package com.smartcity.provider.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store (
    @SerializedName("name")
    @Expose
    var name:String,

    @SerializedName("description")
    @Expose
    var description:String,

    @SerializedName("storeAddress")
    @Expose
    var storeAddress:StoreAddress,

    @SerializedName("provider")
    @Expose
    var provider:Long,

    @SerializedName("telephoneNumber")
    @Expose
    var telephoneNumber:String,

    @SerializedName("defaultTelephoneNumber")
    @Expose
    var defaultTelephoneNumber:String,

    @SerializedName("imageStore")
    @Expose
    var imageStore:String
): Parcelable {
}