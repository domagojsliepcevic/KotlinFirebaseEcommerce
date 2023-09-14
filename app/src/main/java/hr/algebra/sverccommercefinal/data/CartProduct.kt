package hr.algebra.sverccommercefinal.data

/**
 * Data class representing a product in the shopping cart.
 *
 * @property product: The product added to the cart.
 * @property quantity: The quantity of the product in the cart.
 * @property selectedColor: The selected color option for the product (optional).
 * @property selectedSize: The selected size option for the product (optional).
 */
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null
) {
    /**
     * Default constructor with default values.
     */
    constructor() : this(Product(), 1, null, null)
}

