package com.smartcity.provider.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.provider.models.Offer

class ListOfferResponse (
    @SerializedName("result")
    @Expose
    var results: List<Offer>

) {

    override fun toString(): String {
        return "ListOfferResponse(results=$results)"
    }
}