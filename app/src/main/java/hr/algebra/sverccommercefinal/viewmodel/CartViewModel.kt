package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.CartProduct
import hr.algebra.sverccommercefinal.firebase.FirebaseCommon
import hr.algebra.sverccommercefinal.helper.getProductPrice
import hr.algebra.sverccommercefinal.util.Constants.CART_SUBCOLLECTION
import hr.algebra.sverccommercefinal.util.Constants.USER_COLLECTION
import hr.algebra.sverccommercefinal.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class responsible for managing the user's shopping cart data.
 *
 * @property firestore: The instance of FirebaseFirestore for accessing the Firestore database.
 * @property auth: The instance of FirebaseAuth for user authentication.
 * @property firebaseCommon: An instance of FirebaseCommon for common Firebase operations.
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    // Flow representing the user's shopping cart products with resource status.
    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    // Channel for communicating with a delete confirmation dialog.
    private val _deleteDialog = Channel<CartProduct>(Channel.CONFLATED)
    val deleteDialog = _deleteDialog.receiveAsFlow()

    // Flow representing the total price of all cart products.
    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    /**
     * Deletes a cart product from the user's shopping cart.
     *
     * @param cartProduct: The cart product to be deleted.
     */
    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            firestore.collection(USER_COLLECTION).document(auth.uid!!)
                .collection(CART_SUBCOLLECTION).document(documentId).delete()
        }
    }

    /**
     * Calculates the total price of all cart products.
     *
     * @param data: List of cart products.
     */
    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    // List of DocumentSnapshots representing cart product documents.
    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    init {
        // Initialize the ViewModel by fetching cart products.
        getCartProducts()
    }

    /**
     * Fetches the user's shopping cart products from Firestore and updates the [_cartProducts] flow.
     */
    private fun getCartProducts() {
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(CART_SUBCOLLECTION).addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
                }
            }
    }

    /**
     * Changes the quantity of a cart product (increases or decreases) and updates it in Firestore.
     *
     * @param cartProduct: The CartProduct to change the quantity of.
     * @param quantityChanging: The action to perform (increase or decrease the quantity).
     */
    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ) {
        val index = cartProducts.value.data?.indexOf(cartProduct)

        // Check if the index is valid and not -1.
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when (quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch { _deleteDialog.send(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    /**
     * Decreases the quantity of a cart product in Firestore.
     *
     * @param documentId: The ID of the Firestore document representing the cart product.
     */
    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { _, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    /**
     * Increases the quantity of a cart product in Firestore.
     *
     * @param documentId: The ID of the Firestore document representing the cart product.
     */
    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { _, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }
}

