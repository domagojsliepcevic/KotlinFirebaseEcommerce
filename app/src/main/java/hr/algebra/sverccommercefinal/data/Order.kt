package hr.algebra.sverccommercefinal.data

/**
 * Data class representing an order in the shopping system.
 *
 * @property orderStatus: The status of the order (e.g., "Processing," "Shipped," "Delivered").
 * @property totalPrice: The total price of the order.
 * @property product: The list of products included in the order, each represented as a CartProduct.
 * @property address: The shipping address where the order should be delivered.
 */
data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val product: List<CartProduct>,
    val address: Address
)

