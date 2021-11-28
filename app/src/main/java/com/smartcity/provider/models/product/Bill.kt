package com.smartcity.provider.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bill(
    @SerializedName("total")
    @Expose
    var total:Double,

    @SerializedName("alreadyPaid")
    @Expose
    var alreadyPaid:Double,

    @SerializedName("createdAt")
    @Expose
    var createdAt:String
) : Parcelable {
    override fun toString(): String {
        return "Bill(total=$total," +
                "alreadyPaid=$alreadyPaid," +
                "createdAt=$createdAt)"
    }
}