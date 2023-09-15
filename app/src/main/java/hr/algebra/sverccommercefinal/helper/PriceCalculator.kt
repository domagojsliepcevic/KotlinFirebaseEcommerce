package hr.algebra.sverccommercefinal.helper

/**
 * Extension function for calculating the product price after applying an offer percentage.
 *
 * @param offerPercentage: The percentage discount to apply to the original product price.
 *                         A value between 0.0 and 1.0, where 0.0 means no discount and 1.0 means 100% discount.
 * @param price: The original price of the product.
 * @return The calculated price after applying the offer percentage, or the original price if the offerPercentage is null.
 */
fun Float?.getProductPrice(price: Float): Float {
    // If the offer percentage is null, return the original price.
    if (this == null)
        return price

    // Calculate the remaining price percentage after applying the offer.
    val remainingPricePercentage = 1f - this

    // Calculate the price after applying the offer percentage.
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}
