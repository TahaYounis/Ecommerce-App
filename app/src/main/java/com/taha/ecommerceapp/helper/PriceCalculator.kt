package com.taha.ecommerceapp.helper

fun Float?.getProductPrice(price: Float): Float{
    // this --> percentage
    if (this == null)
        return price
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}