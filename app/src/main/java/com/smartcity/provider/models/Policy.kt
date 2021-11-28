package com.smartcity.provider.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Policy(
    @SerializedName("delivery")
    @Expose
    var delivery:Boolean,

    @SerializedName("selfPickUpOption")
    @Expose
    var selfPickUpOption:SelfPickUpOptions,

    @SerializedName("validDuration")
    @Expose
    var validDuration:Long,

    @SerializedName("tax")
    @Expose
    var tax:Int,

    @SerializedName("providerId")
    @Expose
    var providerId:Long,

    @SerializedName("taxRanges")
    @Expose
    var taxRanges:List<TaxRange>

) : Parcelable {
    override fun toString(): String {
        return "Policy(delivery=$delivery," +
                "selfPickUpOption=$selfPickUpOption," +
                "validDuration=$validDuration" +
                "tax=$tax" +
                "providerId=$providerId)"
    }
}