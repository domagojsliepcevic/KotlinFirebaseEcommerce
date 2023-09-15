package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.Order
import hr.algebra.sverccommercefinal.util.Constants.CART_SUBCOLLECTION
import hr.algebra.sverccommercefinal.util.Constants.ORDERS_COLLECTION
import hr.algebra.sverccommercefinal.util.Constants.ORDERS_SUBCOLLECTION
import hr.algebra.sverccommercefinal.util.Constants.USER_COLLECTION
import hr.algebra.sverccommercefinal.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class responsible for managing orders in a shopping system.
 *
 * @property firestore: The instance of FirebaseFirestore for accessing the Firestore database.
 * @property auth: The instance of FirebaseAuth for user authentication.
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    // Flow representing the order placement process with resource status.
    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    /**
     * Places an order in the shopping system.
     *
     * @param order: The order to be placed.
     */
    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        // Use Firestore batch writes to perform multiple operations atomically.
        firestore.runBatch { batch ->
            // Add the order into the user-orders collection
            firestore.collection(USER_COLLECTION)
                .document(auth.uid!!)
                .collection(ORDERS_SUBCOLLECTION)
                .document()
                .set(order)

            // Add the order into orders collection
            firestore.collection(ORDERS_COLLECTION).document().set(order)

            // Delete the products from the user-cart collection
            firestore.collection(USER_COLLECTION).document(auth.uid!!)
                .collection(CART_SUBCOLLECTION).get()
                .addOnSuccessListener {
                    it.documents.forEach { document ->
                        document.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch { _order.emit(Resource.Success(order)) }
        }.addOnFailureListener {
            viewModelScope.launch { _order.emit(Resource.Error(it.message.toString())) }
        }
    }
}
