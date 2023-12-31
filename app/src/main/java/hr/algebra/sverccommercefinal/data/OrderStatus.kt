package hr.algebra.sverccommercefinal.data

/**
 * Sealed class representing the status of an order in a shopping system.
 */
sealed class OrderStatus(val status: String) {
    /**
     * Represents that an order has been placed but not yet processed.
     */
    object Ordered : OrderStatus("Ordered")

    /**
     * Represents that an order has been canceled by the customer.
     */
    object Canceled : OrderStatus("Canceled")

    /**
     * Represents that an order has been confirmed and is in the process of being fulfilled.
     */
    object Confirmed : OrderStatus("Confirmed")

    /**
     * Represents that an order has been shipped and is in transit.
     */
    object Shipped : OrderStatus("Shipped")

    /**
     * Represents that an order has been delivered to the customer.
     */
    object Delivered : OrderStatus("Delivered")

    /**
     * Represents that an order has been returned by the customer.
     */
    object Returned : OrderStatus("Returned")
}

/**
 * Function to get an [OrderStatus] object based on a status string.
 *
 * @param status: The status string to convert to an [OrderStatus] object.
 * @return The corresponding [OrderStatus] object, or [OrderStatus.Returned] if not matched.
 */
fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Ordered" -> OrderStatus.Ordered
        "Canceled" -> OrderStatus.Canceled
        "Confirmed" -> OrderStatus.Confirmed
        "Shipped" -> OrderStatus.Shipped
        "Delivered" -> OrderStatus.Delivered
        else -> OrderStatus.Returned
    }
}


