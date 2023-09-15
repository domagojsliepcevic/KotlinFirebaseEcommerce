package hr.algebra.sverccommercefinal.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a user's address information.
 *
 * @property addressTitle: The title or label for the address (e.g., "Home," "Work").
 * @property fullName: The full name of the recipient.
 * @property street: The street address.
 * @property phone: The phone number associated with the address.
 * @property city: The city where the address is located.
 * @property state: The state or region where the address is located.
 */
@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val street: String,
    val phone: String,
    val city: String,
    val state: String
) : Parcelable {
    /**
     * Default constructor for creating an empty Address object.
     * This constructor initializes all properties with empty strings.
     */
    constructor() : this("", "", "", "", "", "")
}

