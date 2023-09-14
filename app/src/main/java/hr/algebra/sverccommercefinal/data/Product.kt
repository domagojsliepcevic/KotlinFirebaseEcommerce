package hr.algebra.sverccommercefinal.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a product.
 *
 * @property id: Unique identifier for the product.
 * @property name: Name of the product.
 * @property category: Category of the product.
 * @property price: Price of the product.
 * @property offerPercentage: Optional offer percentage for the product.
 * @property description: Optional description of the product.
 * @property colors: Optional list of color options for the product.
 * @property sizes: Optional list of size options for the product.
 * @property images: List of image URLs representing the product.
 *
 * This data class is Parcelable, allowing it to be easily passed between components.
 *
 * @constructor Creates a new instance of [Product].
 */
@Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
) : Parcelable {
    /**
     * Default constructor with default values.
     */
    constructor() : this("0", "", "", 0f, images = emptyList())
}

