package hr.algebra.sverccommercefinal.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

/**
 * Data class representing an order in the shopping system.
 *
 * @property orderStatus: The status of the order (e.g., "Processing," "Shipped," "Delivered").
 * @property totalPrice: The total price of the order.
 * @property products: The list of products included in the order, each represented as a CartProduct.
 * @property address: The shipping address where the order should be delivered.
 * @property date: The date when the order was created in the format "dd-MM-yyyy."
 * @property orderId: A unique identifier for the order, generated based on the current date and total price.
 */
data class Order(
    val orderStatus: String = "",
    val totalPrice: Float = 0f,
    val products: List<CartProduct> = emptyList(),
    val address: Address = Address(),
    val date: String = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
)


