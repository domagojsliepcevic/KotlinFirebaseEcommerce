package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.Product
import hr.algebra.sverccommercefinal.util.Constants.BEST_DEALS_PRODUCT_CATEGORY_VALUE
import hr.algebra.sverccommercefinal.util.Constants.BEST_PRODUCT_CATEGORY_VALUE
import hr.algebra.sverccommercefinal.util.Constants.PRODUCT_COLLECTION
import hr.algebra.sverccommercefinal.util.Constants.CATEGORY_FIELD import hr.algebra.sverccommercefinal.util.Constants.FIRESTORE_DOCUMENT_NAME
import hr.algebra.sverccommercefinal.util.Constants.PRICE_FIELD
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
 * @property bestDealsProducts: StateFlow that represents the state of best deals products data.
 * @property bestProducts: StateFlow that represents the state of best products data.
 */
@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    // Private MutableStateFlow to represent the state of special category products.
    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())

    // Public StateFlow property to expose the special category products state.
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    // Private MutableStateFlow to represent the state of best deals products.
    private val _bestDealsProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())

    // Public StateFlow property to expose the best deals products state.
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    // Private MutableStateFlow to represent the state of best products.
    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())

    // Public StateFlow property to expose the best products state.
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    // Internal data class for managing paging information.
    internal data class PagingInfo(
        var bestProductPage: Long = 1,
        var oldBestProducts: List<Product> = emptyList(),
        var isPagingEnd: Boolean = false
    )

    private val pagingInfo = PagingInfo()

    /**
     * Initialize the ViewModel and trigger the fetching of special category products, best deals, and best products.
     */
    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
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
            .whereEqualTo(CATEGORY_FIELD, SPECIAL_PRODUCT_CATEGORY_VALUE)
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

    /**
     * Fetches the best deals products from Firestore database.
     */
    fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }
        firestore.collection(PRODUCT_COLLECTION)
            .whereEqualTo(CATEGORY_FIELD, BEST_DEALS_PRODUCT_CATEGORY_VALUE)
            .get()
            .addOnSuccessListener { result ->
                val bestDealsProducts = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProducts)) // Emit a success state with the retrieved data.
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(exception.message.toString())) // Emit an error state with the error message.
                }
            }
    }

    /**
     * Fetches the best products from Firestore database.
     */
    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection(PRODUCT_COLLECTION)
                .whereEqualTo(CATEGORY_FIELD, BEST_PRODUCT_CATEGORY_VALUE)
                .orderBy(PRICE_FIELD, Query.Direction.ASCENDING)
                .limit(pagingInfo.bestProductPage * 6)
                .get()
                .addOnSuccessListener { result ->
                    val bestProducts = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = bestProducts == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = bestProducts
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProducts)) // Emit a success state with the retrieved data.
                    }
                    pagingInfo.bestProductPage++
                }
                .addOnFailureListener { exception ->
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(exception.message.toString())) // Emit an error state with the error message.
                    }
                }
        }
    }
}



