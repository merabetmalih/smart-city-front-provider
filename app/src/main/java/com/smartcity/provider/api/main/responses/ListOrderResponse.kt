package com.smartcity.provider.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.provider.models.product.Order

class ListOrderResponse (
    @SerializedName("result")
    @Expose
    var results: List<Order>

) {

    override fun toString(): String {
        return "ListOrderResponse(results=$results)"
    }
}