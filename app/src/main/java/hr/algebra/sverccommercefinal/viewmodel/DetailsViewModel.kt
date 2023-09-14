package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.CartProduct
import hr.algebra.sverccommercefinal.firebase.FirebaseCommon
import hr.algebra.sverccommercefinal.util.Constants.CART_SUBCOLLECTION
import hr.algebra.sverccommercefinal.util.Constants.CART_SUBCOLLECTION_PRODUCT_ID
import hr.algebra.sverccommercefinal.util.Constants.USER_COLLECTION
import hr.algebra.sverccommercefinal.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * ViewModel class responsible for managing product details and cart operations.
 *
 * @property firestore: Firebase Firestore database instance.
 * @property auth: Firebase Authentication instance.
 * @property firebaseCommon: Common Firebase operations utility.
 * @property addToCart: A shared flow representing the result of adding a product to the cart.
 */
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    // A mutable state flow representing the result of adding a product to the cart.
    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asSharedFlow()

    /**
     * Adds or updates a product in the user's cart.
     *
     * @param cartProduct: The product to be added or updated in the cart.
     */
    fun addUpdateProductInCart(cartProduct: CartProduct) {
        // Emit a loading state.
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }

        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(CART_SUBCOLLECTION)
            .whereEqualTo(CART_SUBCOLLECTION_PRODUCT_ID, cartProduct.product.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.let { documents ->
                    if (documents.isEmpty()) {
                        // No matching product found in the cart, add a new product.
                        addNewProduct(cartProduct)
                    } else {
                        val product = documents.first().toObject(CartProduct::class.java)
                        if (product == cartProduct) {
                            // Product already exists in the cart, increase its quantity.
                            val documentId = documents.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else {
                            // Product is different, add a new product.
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle the failure and emit an error state.
                viewModelScope.launch { _addToCart.emit(Resource.Error(e.message.toString())) }
            }
    }

    /**
     * Adds a new product to the cart.
     *
     * @param cartProduct: The product to be added to the cart.
     */
    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addedProduct, e ->
            viewModelScope.launch {
                if (e == null) {
                    // Emit a success state with the added product.
                    _addToCart.emit(Resource.Success(addedProduct!!))
                } else {
                    // Emit an error state with the error message.
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    /**
     * Increases the quantity of a product in the cart.
     *
     * @param documentId: The ID of the cart item to update.
     * @param cartProduct: The product with the updated quantity.
     */
    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, e ->
            viewModelScope.launch {
                if (e == null) {
                    // Emit a success state with the updated product.
                    _addToCart.emit(Resource.Success(cartProduct))
                } else {
                    // Emit an error state with the error message.
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }
}
