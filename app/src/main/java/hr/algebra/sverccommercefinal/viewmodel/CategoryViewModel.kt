package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import hr.algebra.sverccommercefinal.data.Category
import hr.algebra.sverccommercefinal.data.Product
import hr.algebra.sverccommercefinal.util.Constants.CATEGORY_FIELD
import hr.algebra.sverccommercefinal.util.Constants.OFFER_PERCENTAGE_FIELD
import hr.algebra.sverccommercefinal.util.Constants.PRODUCT_COLLECTION
import hr.algebra.sverccommercefinal.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel class responsible for managing data related to a specific category of products.
 *
 * @property firestore: Injected instance of FirebaseFirestore for accessing Firestore database.
 * @property category: The category for which this ViewModel manages product data.
 */
class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {

    // A private mutable state flow for representing the offer products in the category.
    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())

    // A public state flow property that exposes the offer products state.
    val offerProducts = _offerProducts.asStateFlow()

    // A private mutable state flow for representing the best products in the category.
    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())

    // A public state flow property that exposes the best products state.
    val bestProducts = _bestProducts.asStateFlow()

    // Internal data class for managing paging information.
    internal data class PagingInfo(
        var bestProductPage: Long = 1,
        var oldBestProducts: List<Product> = emptyList(),
        var isPagingEnd: Boolean = false
    )

    // Instance of the paging information manager.
    private val pagingInfo = PagingInfo()

    /**
     * Initializes the ViewModel and triggers the fetching of offer products and best products.
     */
    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    /**
     * Fetches the offer products from the Firestore database for the specified category.
     */
    fun fetchOfferProducts() {
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading()) // Emit a loading state.
        }

        // Query Firestore for offer products in the specific category.
        firestore.collection(PRODUCT_COLLECTION)
            .whereEqualTo(CATEGORY_FIELD, category.category)
            .whereNotEqualTo(OFFER_PERCENTAGE_FIELD, null)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products)) // Emit a success state with the retrieved data.
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString())) // Emit an error state with the error message.
                }
            }
    }

    /**
     * Fetches the best products from the Firestore database for the specified category.
     */
    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading()) // Emit a loading state.
            }

            // Query Firestore for best products in the specific category.
            firestore.collection(PRODUCT_COLLECTION)
                .whereEqualTo(CATEGORY_FIELD, category.category)
                .whereEqualTo(OFFER_PERCENTAGE_FIELD, null)
                .limit(pagingInfo.bestProductPage * 6)
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = products == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = products
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(products)) // Emit a success state with the retrieved data.
                    }
                    pagingInfo.bestProductPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString())) // Emit an error state with the error message.
                    }
                }
        }
    }
}
