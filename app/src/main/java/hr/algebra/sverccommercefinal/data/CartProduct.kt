package hr.algebra.sverccommercefinal.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a product in the shopping cart.
 *
 * @property product: The product added to the cart.
 * @property quantity: The quantity of the product in the cart.
 * @property selectedColor: The selected color option for the product (optional).
 * @property selectedSize: The selected size option for the product (optional).
 */
@Parcelize
data class CartProduct(
    val product: Product,          // The product added to the cart.
    val quantity: Int,            // The quantity of the product in the cart.
    val selectedColor: Int? = null,  // The selected color option for the product (optional).
    val selectedSize: String? = null // The selected size option for the product (optional).
) : Parcelable {
    /**
     * Default constructor with default values.
     */
    constructor() : this(Product(), 1, null, null)
}


