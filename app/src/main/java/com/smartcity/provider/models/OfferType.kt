package com.smartcity.provider.models


enum class OfferType{
    PERCENTAGE,
    FIXED
}

object OfferTypeObject {
    val OfferTypes = listOf<Pair<OfferType,String>>(
        Pair(OfferType.PERCENTAGE,"Percentage"),
        Pair(OfferType.FIXED,"Fixed amount"))
}