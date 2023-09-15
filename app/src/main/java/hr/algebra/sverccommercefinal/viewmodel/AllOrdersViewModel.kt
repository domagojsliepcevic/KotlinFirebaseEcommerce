package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.Order
import hr.algebra.sverccommercefinal.util.Constants.ORDERS_SUBCOLLECTION
import hr.algebra.sverccommercefinal.util.Constants.USER_COLLECTION
import hr.algebra.sverccommercefinal.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing a user's order history.
 *
 * @property firestore: The FirebaseFirestore instance for database operations.
 * @property auth: The FirebaseAuth instance for user authentication.
 */
@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    /**
     * A [MutableStateFlow] to represent the list of all user orders with associated loading and error states.
     */
    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())

    /**
     * A public [SharedFlow] property that exposes the list of all user orders.
     */
    val allOrders = _allOrders.asSharedFlow()

    /**
     * Initialize the ViewModel and fetch all user orders.
     */
    init {
        getAllOrders()
    }

    /**
     * Fetch all user orders from the database.
     */
    fun getAllOrders() {
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading()) }

        firestore.collection(USER_COLLECTION)
            .document(auth.uid!!)
            .collection(ORDERS_SUBCOLLECTION)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val orders = querySnapshot.toObjects(Order::class.java)
                viewModelScope.launch { _allOrders.emit(Resource.Success(orders)) }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch { _allOrders.emit(Resource.Error(exception.message.toString())) }
            }
    }
}
