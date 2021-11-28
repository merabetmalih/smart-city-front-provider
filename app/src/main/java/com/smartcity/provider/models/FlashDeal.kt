package com.smartcity.provider.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlashDeal(
    @SerializedName("id")
    @Expose
    var id:Long?,

    @SerializedName("title")
    @Expose
    var title:String?,

    @SerializedName("content")
    @Expose
    var content:String?,

    @SerializedName("providerId")
    @Expose
    var providerId:Long?,

    @SerializedName("createAt")
    @Expose
    var createAt:String?

) : Parcelable {
    override fun toString(): String {
        return "Offer(id=$id," +
                "title=$title," +
                "content=$content," +
                "providerId=$providerId)"
    }

    class CreateFlashError {

        companion object{

            fun mustFillAllFields(): String{
                return "You can't create flash without fill all information."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValidForCreation(): String{
        if(title.isNullOrBlank()
            || title.isNullOrEmpty()
            || content.isNullOrBlank()
            || content.isNullOrEmpty()){
            return CreateFlashError.mustFillAllFields()
        }
        return CreateFlashError.none()
    }
}