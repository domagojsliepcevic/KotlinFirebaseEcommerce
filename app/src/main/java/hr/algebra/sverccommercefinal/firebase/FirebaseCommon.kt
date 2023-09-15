package hr.algebra.sverccommercefinal.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.algebra.sverccommercefinal.data.CartProduct
import hr.algebra.sverccommercefinal.util.Constants

/**
 * Helper class for common Firebase Firestore and Authentication operations.
 *
 * @property firestore: The instance of FirebaseFirestore used for database operations.
 * @property auth: The instance of FirebaseAuth used for user authentication.
 */
class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    // Reference to the user's cart collection in Firestore.
    private val cartCollection = firestore.collection(Constants.USER_COLLECTION)
        .document(auth.uid!!)
        .collection(Constants.CART_SUBCOLLECTION)

    /**
     * Adds a product to the user's cart in Firestore.
     *
     * @param cartProduct: The product to be added to the cart.
     * @param onResult: Callback function to handle the result of the operation.
     *                  It provides the added CartProduct or an Exception in case of an error.
     */
    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document()
            .set(cartProduct)
            .addOnSuccessListener {
                // Successfully added the product to the cart.
                onResult(cartProduct, null)
            }
            .addOnFailureListener { exception ->
                // An error occurred while adding the product to the cart.
                onResult(null, exception)
            }
    }

    /**
     * Increases the quantity of a product in the user's cart in Firestore.
     *
     * @param documentId: The document ID of the product in the cart collection.
     * @param onResult: Callback function to handle the result of the operation.
     *                  It provides the updated document ID or an Exception in case of an error.
     */
    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transaction ->
            val documentReference = cartCollection.document(documentId)
            val document = transaction.get(documentReference)

            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentReference, newProductObject)
            }
        }
            .addOnSuccessListener {
                // Successfully increased the quantity of the product.
                onResult(documentId, null)
            }
            .addOnFailureListener { exception ->
                // An error occurred while increasing the quantity of the product.
                onResult(null, exception)
            }
    }

    /**
     * Decrease the quantity of a product in the user's cart in Firestore.
     *
     * @param documentId: The document ID of the product in the cart collection.
     * @param onResult: Callback function to handle the result of the operation.
     *                  It provides the updated document ID or an Exception in case of an error.
     */
    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transaction ->
            val documentReference = cartCollection.document(documentId)
            val document = transaction.get(documentReference)

            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentReference, newProductObject)
            }
        }
            .addOnSuccessListener {
                // Successfully decreased the quantity of the product.
                onResult(documentId, null)
            }
            .addOnFailureListener { exception ->
                // An error occurred while decreasing the quantity of the product.
                onResult(null, exception)
            }
    }

    enum class QuantityChanging{
        INCREASE,DECREASE
    }
}
