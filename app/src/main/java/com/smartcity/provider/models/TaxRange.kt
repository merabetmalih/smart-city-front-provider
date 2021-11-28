package com.smartcity.provider.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaxRange (
    @SerializedName("startRange")
    @Expose
    var startRange:Double,

    @SerializedName("endRange")
    @Expose
    var endRange:Double,

    @SerializedName("fixAmount")
    @Expose
    var fixAmount:Double
): Parcelable {
    override fun toString(): String {
        return "TaxRange(startRange=$startRange," +
                "endRange=$endRange," +
                "fixAmount=$fixAmount)"
    }
}