package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.Product
import hr.algebra.sverccommercefinal.util.Constants.PRODUCT_COLLECTION
import hr.algebra.sverccommercefinal.util.Constants.SPECIAL_PRODUCT_CATEGORY_FIELD
import hr.algebra.sverccommercefinal.util.Constants.SPECIAL_PRODUCT_CATEGORY_VALUE
import hr.algebra.sverccommercefinal.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class responsible for managing data related to special category products.
 *
 * @property firestore: Injected instance of FirebaseFirestore for accessing Firestore database.
 * @property specialProducts: StateFlow that represents the state of special category products data.
 */
@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    // Private MutableStateFlow to represent the state of special category products.
    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())

    // Public StateFlow property to expose the special category products state.
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    /**
     * Initialize the ViewModel and trigger the fetching of special category products.
     */
    init {
        fetchSpecialProducts()
    }

    /**
     * Fetches the special category products from Firestore database.
     */
    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading()) // Emit a loading state.
        }

        // Query Firestore for special products in the specific category.
        firestore.collection(PRODUCT_COLLECTION)
            .whereEqualTo(SPECIAL_PRODUCT_CATEGORY_FIELD, SPECIAL_PRODUCT_CATEGORY_VALUE)
            .get()
            .addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList)) // Emit a success state with the retrieved data.
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(exception.message.toString())) // Emit an error state with the error message.
                }
            }
    }
}
